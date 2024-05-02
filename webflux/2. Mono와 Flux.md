## Mono 와 Flux

> **`Mono`** 와 **`Flux`** 의 차이는, 내부의 아이템의 수이다.<br>
**`Flux`** 는 0개~N개의 아이템을 방출할 수 있는 객체라면,<br>
**`Mono`** 는 0개~1개의 아이템을 방출할 수 있는 객체이다.

### Mono
> 0부터 1개의 item을 **`subscriber`** 에게 전달한다.
**`subscriber`** 에게 **onComplete**, **onError** signal을 전달하면 연결이 종료된다.
**onNext**가 호출되면 곧바로 onComplete 이벤트가 전달된다.

### Flux
> 0부터 n개의 item을 subscriber에게 전달한다.
`subscriber`에게 **onComplete**, **onError** signal을 전달하면 연결이 종료된다.

<br>

### ❓ Flux도 하나의 값을 보낼 수 있는데, 그럼 Mono는 왜 필요할까?
물론, Flux가 Mono의 역할을 대부분 대체할 수 있다고 생각한다. 그럼에도 Mono가 필요한 이유는 다음과 같다.

- #### 반드시 하나의 값을 필요로 하는 경우
> 예시) <br>
  유저가 작성한 게시글의 숫자 <br>
  http 응답 객체

- ####  값이 하나이므로 onNext 이후 바로 onComplete를 호출하면 되기때문에 구현이 간단하다.
- ####  Subscriber도 최대 1개의 item이 전달된다는 것이 보장되므로 더 간결하게 코드작성이 가능.
