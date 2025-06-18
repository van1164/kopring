# flatMap vs flatMapMany

#### 개념 정리

Spring WebFlux에서는 두 가지 주된 reactive 타입이 있다.

* `Mono<T>`: 0 또는 1개의 값을 비동기적으로 전달
* `Flux<T>`: 0개 이상의 스트림을 비동기적으로 전달

그에 따라 `flatMap`과 `flatMapMany`도 다르게 사용한다.

| 함수            | 입력 타입                 | 출력 타입     | 설명                  |
| ------------- | --------------------- | --------- | ------------------- |
| `flatMap`     | `Mono<T>` → `Mono<R>` | `Mono<R>` | 단일 비동기 결과를 변환       |
| `flatMapMany` | `Mono<T>` → `Flux<R>` | `Flux<R>` | 단일 결과에서 다중 스트림으로 확장 |

#### 예시 코드

```kotlin
fun getUserBoardPermissions(userId: Long): Flux<Board> {
    return boardPermissionRepository.findByUserId(userId)
        .map { it.boardId }
        .collectList() // Mono<List<Long>>
        .flatMapMany { boardIds ->
            boardService.getBoards(boardIds) // Flux<Board>
        }
}
```

#### 핵심 포인트

* `collectList()`는 `Mono<List<T>>`를 반환한다.
* 이 `List<T>`를 바탕으로 다시 `Flux<T>`로 작업하고 싶다면 **flatMapMany를 써야 한다**.
