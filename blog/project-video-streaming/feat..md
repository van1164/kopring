# 댓글 좋아요 구현 (feat. 동시성 문제)

![](https://velog.velcdn.com/images/van1164/post/b555b8bb-7625-4aaa-a9eb-bd6f42fcb975/image.jpg)

### 좋아요 기능 구현과 동시성문제 <a href="#undefined" id="undefined"></a>

기능은 간단하다. 댓글에 좋아요 버튼을 누르면 좋아요가 1올라가는 동작을 한다. 우선 내가 구현한 코드는 다음과 같다.

```kotlin
@Transactional
fun likeComment(name: String, commentId: Long): Mono<ResponseEntity<Any>> {
    return commentRepository.findById(commentId)
        .doOnNext {
            it.good +=1
        }
        .flatMap {
            commentRepository.save(it)
        }
        .thenReturn<ResponseEntity<Any>?>(ResponseEntity.ok().build())
        .onErrorReturn(ResponseEntity.internalServerError().build())
}
```

기능이 간단한 만큼 코드도 간단하다. 하나의 요청을 하였을 때에는 문제가 없다.

**하지만, 여러 요청이 동시에 들어온다면 어떨까?**

#### K6를 통한 동시성 테스트 <a href="#k6" id="k6"></a>

```javascript
import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  vus: 10,
  duration: '10s',
};

export default function() {
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  http.get('http://localhost:8080/api/v1/comment/like/22',params);
}
```

![](https://velog.velcdn.com/images/van1164/post/d0f9f094-23b8-4630-928d-3214504ba8eb/image.png)

총 2073개의 요청을 10초동안 보냈다.

![](https://velog.velcdn.com/images/van1164/post/0f55c9ab-1723-4b96-b7a2-367ffe57837a/image.png)

하지만 좋아요는 672개밖에 올라가지 않았다. 이는 동시성문제일 것이다.

#### 동시성 문제 <a href="#undefined" id="undefined"></a>

아래 그림으로 설명한 것과 같이 동시에 조회한 좋아요개수가 같은 두개의 요청에 1을 더한 결과가 같게 업데이트되기 때문에 이러한 문제가 발생한다.\


<figure><img src="https://velog.velcdn.com/images/van1164/post/c944b642-4e5f-4207-9e44-f79fd8a70495/image.png" alt=""><figcaption></figcaption></figure>

***

### 문제 해결 과정 <a href="#undefined" id="undefined"></a>

#### 1. Isolation을 통한 격리 수준 설정 <a href="#id-1-isolation" id="id-1-isolation"></a>

제일 처음으로 고려했던 방식은 격리수준을 RepeatableRead나 Serializable로 높게 지정해서 안전성을 올리는 방식이었다. 물론 성능이 매우 저하될 것이기 때문에 정합성이 보장되는지 부터 확인해보고자 하였다.

**SERIALIZABLE 격리수준**

```kotlin
@Transactional(isolation = Isolation.SERIALIZABLE)
```

다음과 같이 설정해주고 테스트를 해보았다.

\
10초동안 1485번의 요청을 보냈고 그중 473개의 요청만 성공하였다. 데이터베이스에는 473개의 좋아요가 오른 것으로 보았을 때 정합성의 문제는 없지만 약 1000개의 요청이 데드락으로 실패했기 떄문에 성능상 좋지 않다.

<figure><img src="https://velog.velcdn.com/images/van1164/post/2136e47b-4ff9-4c78-8fa9-e008854e059c/image.png" alt=""><figcaption></figcaption></figure>

**RepeatableRead 격리수준**

```kotlin
@Transactional(isolation = Isolation.REPEATABLE_READ)
```

![](https://velog.velcdn.com/images/van1164/post/bc581506-abf4-4935-81b3-9fe0208fae50/image.png)

**2235개**의 요청이 모두 성공했으나, 실제 좋아요는 **629개**밖에 오르지 않았다.

***

#### 2. 비관적 락 사용 <a href="#id-2" id="id-2"></a>

```kotlin
@Lock(LockMode.PESSIMISTIC_WRITE)
```

다음과 같이 비관적 쓰기락을 통해 select for update와 같은 쿼리를 날릴 수 있도록 하였다.\


<figure><img src="https://velog.velcdn.com/images/van1164/post/f5a5f581-c896-4873-a2f5-4f60f8d309fc/image.png" alt=""><figcaption></figcaption></figure>

**1310개**의 요청을 모두 성공하였고, 좋아요 수도 **1310개**로 정확하게 올랐다. 정합성과 성능의 어느 중간선을 만족한 것 같아서 나쁘진 않지만, 확실히 사용자가 아무생각없이 누를 수 있는 좋아요 기능에 대해 성능이 별로가 아닌가하는 생각이 든다.

***

#### 3. 좋아요 테이블 분리와 Count 사용 <a href="#id-3-count" id="id-3-count"></a>

**좋아요 테이블**

```kotlin
@Table("comment_like")
data class CommentLike(
    val commentId : Long,
    val userId : String,
    @Id
    val id : Long? = null
)
```

**레포지토리**

```kotlin
@Repository
interface CommentRepository : R2dbcRepository<Comment,Long> {
    @Query("select c.video_id,c.user_name,c.message,c.created_date,c.id, c.bad ,Count(l.comment_id) as good from comment c join comment_like l on c.id = l.comment_id where c.id =:id group by l.comment_id")
    override fun findById(id : Long): Mono<Comment>
}
```

좋아요 테이블과 JOIN한 후에 row수를 good컬럼으로 매핑해서 불러온다.

**서비스**

```kotlin
@Transactional
fun likeComment(userName: String, commentId: Long): Mono<ResponseEntity<Any>> {
    return CommentLike(commentId,userName)
        .toMono()
        .flatMap {
            commentLikeRepository.save(it) // 열 추가
        }
        .thenReturn<ResponseEntity<Any>?>(ResponseEntity.ok().build())
        .onErrorReturn(ResponseEntity.internalServerError().build())
}
```

위 코드에서 처럼 좋아요를 누르면 좋아요 수를 update하는 것이 아닌 insert하고 조회할 때 댓글에 대한 좋아요 수를 `Count`를 통해 조회한다.

![](https://velog.velcdn.com/images/van1164/post/1cb2610d-c8fa-4861-a00b-7252c5f2dae4/image.png)

10초동안 총 **2273개**의 요청이 보내졌고 모든 요청에 대해 성공했다. 좋아요 row도 **2273개**가 생성되었다.

좋아요 누르는 기능에 대한 성능은 2배가량 증가하였다. 하지만, 조회하는 방식이 달라졌으니 조회할때의 성능도 알아봐야 한다.

#### 단일 테이블 조회 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/d550a9a3-4846-45cd-9eec-305bd8db0df2/image.png)

#### join을 통한 좋아요컬럼과 함께 조회 <a href="#join" id="join"></a>

![](https://velog.velcdn.com/images/van1164/post/754f2ad2-03b1-41b8-91ca-1e48c6db0811/image.png)

> ~~오히려 join을 통해 좋아요 row수로 좋아요 컬럼을 조회하는 방식이 평균 성능이 훨씬 좋았다. 따라서 이 방식을 통해 구현하는 것으로 확정지었다.~~\
> 100만건의 좋아요 데이터를 넣고 수행해보니 평균 응답이 1초이상 걸렸다.. 레디스를 사용하는 전략으로 최근에 변경중이다.

### 결론 <a href="#undefined" id="undefined"></a>

댓글 좋아요 기능을 구현을 하던 중 동시성 문제가 발생하였다.\
따라서 처음에는 격리수준을 조정하여 정합성을 올려보았으나 속도가 매우 느려 db 데드락으로 인해 실패하는 일이 잦았다.\
두번째로는 비관적 락을 사용해서 쓰기락을 걸었다. 정합성도 지켰고 데드락으로 실패하는 요청도 없었으나 여전히 속도가 느렸다.\
마지막으로 좋아요테이블을 분리한 후에 COUNT 조회를 통해 좋아요 수를 세었다. 이렇게 구현하니 속도도 2배가량 늘었으며 사용자가 좋아요를 누른 댓글을 조회할 수도 있게 되었다.

**하지만**\
100만건의 좋아요 데이터를 넣고 수행해보니 평균 응답이 1초이상 걸렸다.. 레디스를 사용하는 전략으로 최근에 변경중이다.
