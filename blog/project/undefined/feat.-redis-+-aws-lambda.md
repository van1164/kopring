# 좋아요, 조회수 기능 최적화 (feat. Redis + AWS Lambda)

![](https://velog.velcdn.com/images/van1164/post/fd1246f3-93ee-4c8e-ace4-7b717ff8c4f8/image.png)

### JOIN + COUNT 를 통한 좋아요 수의 문제 <a href="#join-count" id="join-count"></a>

{% content-ref url="feat..md" %}
[feat..md](feat..md)
{% endcontent-ref %}

위 포스트에서 JOIN을 통한 좋아요 수를 구현하였다. 하지만 이는 좋아요가 50만개 이상 생길경우 평균 응답 속도가 10초 이상으로 사용하기 어려웠다. 특히나 조회수도 구현할 예정인데, 조회수도 다음과 같이 구현한다면 더 오랜 시간이 걸릴 것으로 예상된다.

### Redis를 활용한 조회 수 구현 <a href="#redis" id="redis"></a>

{% content-ref url="../../back-end/redis/feat.-redis.md" %}
[feat.-redis.md](../../back-end/redis/feat.-redis.md)
{% endcontent-ref %}

위 포스트에서 Redis의 increment를 사용해서 조회 수를 증가 시키고 1분마다 데이터베이스에 업데이트해주는 구현을 진행하였다.

이렇게 구현하니 30초동안 3만개의 요청에 대해서 좋아요 누르는 기능에 대한 평균 응답 속도는 100ms 이하로 나오며 조회할 때도 join없이 그냥 row만 읽어와서 빠르게 조회가 가능했다.

#### 좋아요 기능 서비스 <a href="#undefined" id="undefined"></a>

```kotlin
@Service
class VideoLikeService(
    private val videoLikeRepository: VideoLikeRepository,
    private val redisR2dbcRepository: RedisR2dbcRepository
) {

    fun videoLike(userName: String, videoId: Long): Mono<ResponseEntity<Long>> {
        return VideoLike(videoId,userName).toMono()
            .flatMap {
                videoLikeRepository.save(it)
            }.flatMap {
                redisR2dbcRepository.increment(VIDEO_LIKE_PREFIX+videoId)
            }
            .map {
                ResponseEntity.ok(it)
            }
            .onErrorReturn(ResponseEntity.internalServerError().build())
    }
}
```

### 스케쥴 서비스의 문제점 <a href="#undefined" id="undefined"></a>

위 포스팅에서 작성한 것과 같이 1분마다 redis에서 좋아요수를 불러와 mysql을 업데이트하도록 구현하였다. 이 부분에서 문제점은 크게 2가지가 있었다

* **스프링 어플리케이션 내에서 리소스를 계속 잡아먹는다.**
* **MySQL의 주기적인 Lock으로 인한 성능 저하**

### AWS Lambda를 사용한 리소스 분리 + 격리수준을 READ UNCOMMITED로 낮춤. <a href="#aws-lambda-read-uncommited" id="aws-lambda-read-uncommited"></a>

#### AWS Lambda + CloudWatch를 통해 주기적인 업데이트 <a href="#aws-lambda-cloudwatch" id="aws-lambda-cloudwatch"></a>

CloudWatch는 트리거 추가를 통해 쉽게 추가할 수 있다.\
또한 업데이트시에 **Uncommited Read** 격리수준을 적용해서 락없이 업데이트를 하도록 구현해서 성능 저하를 막도록 하였다. 좋아요수나 조회수는 이 기능에서만 수정되기 때문에 이렇게 격리수준을 낮춘다고 하더라도 문제가 발생하지 않을 것으로 판단했다.

![](https://velog.velcdn.com/images/van1164/post/5a3f76f9-aea9-4d1f-93ed-e6d8bb7fd44b/image.png)

#### 파이썬 코드 작성 (Feat. Uncommited Read) <a href="#feat-uncommited-read" id="feat-uncommited-read"></a>

```python
import json
import redis
import pymysql

def lambda_handler(event, context):
    # Redis 연결 설정
    r = redis.Redis(host='url', port=6379, password='password', db=0)
    
    # MySQL 연결 설정
    connection = pymysql.connect(host='url',
                                 user='user',
                                 password='password',
                                 db='streaming')
    
    try:
        with connection.cursor() as cursor:

            cursor.execute("SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;")
            
            # Redis에서 VIDEO_LIKE::로 시작하는 키 가져오기
            keys = r.keys(pattern="VIDEO_LIKE::*")
            for key in keys:
                value = r.get(key).decode('utf-8')
                key = key.decode('utf-8').replace("VIDEO_LIKE::","")
                # MySQL 업데이트
                sql = "update video_r2dbc set good = {} where id ={}".format(value,key)
                cursor.execute(sql)
                
        connection.commit()
    finally:
        connection.close()
    
    return {
        'statusCode': 200,
        'body': json.dumps('동기화 성공')
    }

```

#### AWS Lambda에 파이썬 모듈 추가 <a href="#aws-lambda" id="aws-lambda"></a>

이 코드가 동작하기 위해서는 redis와 pymysql 모듈이 필요하기 때문에 Layer를 추가해줘야한다. 이 과정은 잘 설명된 블로그가 있어서 이를 남긴다.\
[https://bosungtea9416.tistory.com/entry/AWS-Lambda%EC%97%90-request-%EB%AA%A8%EB%93%88-%EC%B6%94%EA%B0%80%ED%95%98%EA%B8%B0](https://bosungtea9416.tistory.com/entry/AWS-Lambda%EC%97%90-request-%EB%AA%A8%EB%93%88-%EC%B6%94%EA%B0%80%ED%95%98%EA%B8%B0)

#### AWS Lambda에 코드 작성 <a href="#aws-lambda" id="aws-lambda"></a>

![](https://velog.velcdn.com/images/van1164/post/7891d53c-8919-431d-9eca-7db46873d793/image.png)

#### 주기적으로 업데이트 되는 것 확인 <a href="#undefined" id="undefined"></a>

100명의 동시 접속자에 대해서 3만개의 좋아요 요청을 보냈고 정합성문제 없이 잘 업데이트되는 것을 확인할 수 있었다.

***

### 결론 <a href="#undefined" id="undefined"></a>

AWS Lambda와 Redis를 활용해서 조회수와 좋아요 수를 주기적으로 업데이트하면서도 내부 리소스를 낭비하지 않도록 구현에 성공하였다. 또한 Update 쿼리시 Lock을 방지해서 성능을 최대한 끌어 올렸다.
