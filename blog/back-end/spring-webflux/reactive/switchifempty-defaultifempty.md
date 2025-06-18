# switchIfEmpty 와 defaultIfEmpty 차이

### switchIfEmpty <a href="#switchifempty" id="switchifempty"></a>

> 데이터가 지나갈 때 빈값이 오면 다른 방출 값으로 대체해서 내보낸다.

![](https://velog.velcdn.com/images/van1164/post/12a8bdd9-baed-4d31-8ca0-5489741334aa/image.svg)

### 코드 예제 <a href="#undefined" id="undefined"></a>

```kotlin
fun main() {
    val sourceMono: Mono<String> = Mono.empty() // 빈 Mono

    val resultMono: Mono<String> = sourceMono
        .switchIfEmpty(Mono.just("default value"))

    resultMono.subscribe { println(it) } // Output: Processed: default value
}
```

### defaultIfEmpty <a href="#defaultifempty" id="defaultifempty"></a>

> 데이터가 지나갈 때 빈값이 오면 다른 값으로 대체한다.

![](https://velog.velcdn.com/images/van1164/post/6586c672-1881-4e4d-bb2f-8ebef5990dff/image.svg)

### 코드 예제 <a href="#id-1" id="id-1"></a>

```kotlin
fun main() {
    val sourceMono: Mono<String> = Mono.empty() // 빈 Mono

    val resultMono: Mono<String> = sourceMono
        .defaultIfEmpty("default value")

    resultMono.subscribe { println(it) } // Output: default value
}
```

### 차이점 <a href="#undefined" id="undefined"></a>

얼핏보면 기본값을 지정해서 응답하는 동일한 동작을 하는 것 같지만 둘은 큰 차이점이 있다.\
`switchIfEmpty` 는 대체 값으로 Publisher를 받는다. 그렇기 때문에 반환하는 내부로직이 리액터의 장점을 받을 수 있다. `defaultIfEmpty`는 그냥 반환값을 받기때문에 Publisher가 들어갈 수 없다.

비교하자면 `map`과 `flatMap`의 차이라고 생각하면 이해가 빨리되었다.
