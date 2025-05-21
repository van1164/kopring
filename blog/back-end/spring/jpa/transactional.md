# @Transactional의 내부 동작 과정

![](https://velog.velcdn.com/images/van1164/post/ec8daa55-8caf-42d3-b180-2cd520035422/image.png)

## 트랜잭션

데이터 베이스에 데이터를 저장할 때 작업을 할 때마다 쿼리를 실행하지 않고 트랜잭션이라는 작업의 단위를 지정해서 단위마다 쿼리를 실행한다.

### 성능상의 장점

예를 들어, 여행 엔티티를 조회해서 이름을 100번 바꾸는 함수가 있다고하자.

```kotlin
    @Transactional
    fun updateFuntion(){
        val trip = tripRepository.findById(1).orElseThrow()
        (1..100).forEach {
            trip.name = it.toString()
        }
    }
```

이 함수에서는 trip의 이름을 총 100번 변경하였다. 그렇다면 SQL 쿼리는 몇번 나갈까?

```
select t1_0.id,t1_0.name from trip t1_0 where t1_0.id=1;
update trip set name='100' where id=1;
```

처음에 trip을 조회하는 쿼리 하나와 마지막으로 변경되었던 이름 100에 대해 update쿼리가 나갔다. 데이터베이스에 쿼리를 날리는 과정은 네트워크 통신을 요하는 일이기 때문에 많으면 많을 수록 서비스 성능에 악영향을 끼치기 때문에 적절한 단위로 요청을 보내는 것이 중요하다.

### RollBack을 통한 일관성 유지

다음과 같은 trip을 삭제하고 그에 필요한 함수를 실행하고 나서 성공 메시지를 보내는 함수가 있다고 가정하자.

```kotlin
    @Transactional
    fun deleteFunction(): String {
        try {
            val trip = tripRepository.findById(1).orElseThrow()
            tripRepository.delete(trip)
            otherDeleteFuntion()
            return "성공적으로 삭제했습니다."
        }   catch (e:Exception){
            return "실패하였습니다."
        }
    }
```

이 때 `otherDeleteFuntion`에서 에러가 발생하여 삭제를 완료하지 못한 상황에서 트랜잭션이 없다고 가정한다면 \*\*"실패하였습니다."\*\*라는 문구가 반환되었지만 trip은 삭제되고 `otherDeleteFuntion`은 실행되지 않은 어정쩡한 상태로 남는다. 이렇게 되면 완벽하게 trip이 지워졌다고 볼 수없는 애매한 상황이 된다. 하지만, `@Transactional`을 통해 트랜잭션으로 관리한다면 중간에 오류가 발생하였을 때, 트랜잭션내에서 했던 동작을 원래 상태로 되돌린다(즉, 커밋하지 않는다).

## 이게 가능한 이유

Spring에서는 `@Transactional`을 통해 트랜잭션 단위를 지정하는데, 이 어노테이션을 지정한 함수 내에서 조회하면 엔티티를 영속성 컨텍스트(1차 캐시)와 스냅샷에 저장해두었다가 트랜잭션내에서 1차캐시의 값을 수정하고 트랜잭션이 끝나면 1차 캐시값과 스냅샷을 비교해서 다르면 Update 쿼리를 실행한다.&#x20;

<figure><img src="../../../.gitbook/assets/image (1) (1) (1) (1).png" alt=""><figcaption></figcaption></figure>

**1. 조회할 때 1차캐시와 스냅샷에 초기상태 저장**

**2. 데이터를 수정하면 1차캐시값을 수정**

**3. 트랜잭션이 끝났을 때 Commit과 flush를 통해 1차캐시값과 그 값으 스냅샷을 비교해서 다르거나 추가된걸 Update, Insert**

**만약 중간에 오류가 발생하면 Commit과 flush를 하지 않고 종료**
