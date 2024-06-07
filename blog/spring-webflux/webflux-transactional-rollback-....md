# Webflux에서 @Transactional이 Rollback을 안해요...

![](https://velog.velcdn.com/images/van1164/post/913af02b-5d45-4daf-90cc-dbf2cce2e8b9/image.png)

### 문제상황 <a href="#undefined" id="undefined"></a>

구독상황을 저장하는 코드에서 저장 이후 중간에 오류가 생기면 롤백시켜야 할텐데 현재 코드에서 일부러 중간에 오류를 발생시켜도 롤백이 되지 않았다..

### 코드 <a href="#undefined" id="undefined"></a>

```java
@Transactional
fun subscribe(fromUserId: String, toUserId: String): Mono<ResponseEntity<String>> {
    return userRepository.findFirstByUserId(fromUserId)
        //생략
        .flatMap(subscribeRepository::save)
        .flatMap{
            Mono.error<UserSubscribe>(Exception())  // 일부러 발생시킨 오류
        }
        .map {
            ResponseEntity.ok().body("success")
        }
        .onErrorResume {
            when(it){
                is NotFoundException -> ResponseEntity.badRequest().body("존재하지 않는 User").toMono()
                is AlreadySubscribeException -> ResponseEntity.badRequest().body("이미 구독 중").toMono()
                else ->  ResponseEntity.internalServerError().body("fail").toMono()
            }
        }
}
```

물론 트랜잭션 자체가 동작하지 않는 것은 아니었다. @Transactional을 적용한 범위내에서 트랜잭션은 잘 이루어졌다. 다만, 중간에 오류가 났을 때 rollback이 되지 않았다. 검색을 통해 여러 블로그를 찾아보면서 R2dbc TrasactionManager도 직접 정의해봤지만 해결되지 않았다..

그러다 공식 Spring 블로그에서 해답을 찾을 수 있었다!!

[https://spring.io/blog/2019/05/16/reactive-transactions-with-spring](https://spring.io/blog/2019/05/16/reactive-transactions-with-spring)

### TransactionalOperator 사용 <a href="#transactionaloperator" id="transactionaloperator"></a>

```java
fun subscribe(fromUserId: String, toUserId: String): Mono<ResponseEntity<String>> {
    return userRepository.findFirstByUserId(fromUserId)
        //생략
        .flatMap(subscribeRepository::save)
        .flatMap{
            Mono.error<UserSubscribe>(Exception())  // 일부러 발생시킨 오류
        }
        .map {
            ResponseEntity.ok().body("success")
        }
        .`as`(transactionalOperator::transactional) // 트랜잭션 추가
        .onErrorResume {
            when(it){
                is NotFoundException -> ResponseEntity.badRequest().body("존재하지 않는 User").toMono()
                is AlreadySubscribeException -> ResponseEntity.badRequest().body("이미 구독 중").toMono()
                else ->  ResponseEntity.internalServerError().body("fail").toMono()
            }
        }
}
```

`.as(transactionalOperator::transactional)` 이 구문을 추가하면 트랜잭션도 적용되면서 중간에 오류 발생시 롤백도 가능했다.

@Transactional이 좀 더 명확하게 다가오는 느낌이 있기도하고 블로그를 보면 원래는 동작해야하는 것으로 보이는데 이를 사용하면서도 해결할 수있는 방법을 찾아서 추가해볼 예정이다.
