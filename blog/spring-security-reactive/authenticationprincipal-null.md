# @AuthenticationPrincipal에 null값 들어오는 문제

![](https://velog.velcdn.com/images/van1164/post/09a8f5cf-cfd0-4dff-98c5-caec57238df5/image.png)

### @AuthenticationPrincipal 유저정보 불러오기 <a href="#authenticationprincipal" id="authenticationprincipal"></a>

```kotlin
    fun createComment(
        @AuthenticationPrincipal principalDetails: PrincipalDetails,
```

위와 같이 `@AuthenticationPrincipal`를 사용하면 Security context내에 저장해두었던 사용자 정보를 불러올 수 있다.

### 오류 발생 <a href="#undefined" id="undefined"></a>

`principalDetails`가 null값이 들어왔다는 문제가 발생하였다.\
하지만, 분명 `PrincipalDetails`가 null이면 그 이전에 401 코드를 반환하고 끝냈어야하는데, 이러한 오류가 발생하는 것이 이해가 가지않았다.

### 처음으로 한 생각 <a href="#undefined" id="undefined"></a>

401코드를 반환하지 않은 이유는 Security Context에 들어갔기 때문일 것이다. 하지만 그렇다면 `PrincipalDetails`가 null일 수 없다고 생각해서 Security Context부터 확인해보았다.

***

#### Context 꺼내보기 <a href="#context" id="context"></a>

```kotlin
println(SecurityContextHolder.getContext().toString())
```

이렇게 출력해보니

```
SecurityContextImpl [Authentication=UsernamePasswordAuthenticationToken [ // 생략
```

위와 같이 Context가 들어있었다.

***

### 왜 PrincipalDetails가 null 일까 <a href="#principaldetails-null" id="principaldetails-null"></a>

전혀 이유를 모르겠다가 다음과 같은 블로그글을 보게 되었다.\
[https://devjem.tistory.com/70](https://devjem.tistory.com/70)

요약하면 SecurityContext에 저장하는 `Authentication`과 @AuthenticationPrincipal에서 불러오는 `Authentication`이 서로 다르기 때문에 발생한다는 것이다.

하지만 @AuthenticationPrincipal 내부 코드들을 계속 보니 이 둘을 비교하는 코드는 없었다. 다만 controller에서 @AuthenticationPrincipal 어노테이션이 내가 만든 PrincipalDetails에 붙어있었기 때문에 `PrincipalDetails`가 리턴되지 않기 때문이라고 판단하였다.

***

나의 코드에서는 getAuthentication을 통해 jwt를 Authentication을 반환할 때에는 아래와 같이 jwt에 저장한 subject로 Authentication을 리턴하였다.

```kotlin
fun getAuthentication(token: String): Mono<UsernamePasswordAuthenticationToken> { 
	val claims = getClaims(token)
	val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.")
    val userId = claims.subject
    val authorities: Collection<GrantedAuthority> = (auth.toString())
            .split(",")
            .map { SimpleGrantedAuthority(it) }
 	val principal = User(userId,"",authorities)
    
	return UsernamePasswordAuthenticationToken(principal,"",authorities)
}
```

**하지만,** @AuthenticationPrincipal 을 통해서 PrincipalDetails를 가져올 때는 다음과 같은 코드를 사용한다.

```kotlin
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userService.findByUserId(username)
            .map{
                return@map PrincipalDetails(it)
            }
    }
```

즉, getAuthentication이 가지는 principal은 `User` 타입이기 때문에 인증은 되었으나`PrincipalDetails` 타입은 null인 것이다.

따라서 getAuthentication을 수정하였다.

```kotlin
    fun getAuthentication(token: String): Mono<UsernamePasswordAuthenticationToken> {
        val claims = getClaims(token)
        val auth = claims["auth"] ?: throw RuntimeException("잘못된 토큰입니다.")
        val userId = claims.subject
        val authorities: Collection<GrantedAuthority> = (auth.toString())
            .split(",")
            .map { SimpleGrantedAuthority(it) }

       return userService.findByUserId(userId)
           .map{
               PrincipalDetails(it)
           }
           .map{principal->
               UsernamePasswordAuthenticationToken(principal,"",authorities)
           }
    }
```

수정하니 잘 동작하였다!!

### 결론 <a href="#undefined" id="undefined"></a>

context에 저장하는 Principal의 타입을 UserDetails를 상속해서 커스텀한 타입으로 반환하지 않았고, `@AuthenticationPrincipal` 어노테이션을 가진 파라미터의 타입은 커스텀한 타입으로 지정하였기 때문에 문제가 발생하였다.\
context에는 값이 들어갔기 때문에 인증은 되었으나 `@AuthenticationPrincipal`에는 null이 들어간 것이다.
