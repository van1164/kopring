# Publisher와 Subscriber

## Reactor <a href="#reactor" id="reactor"></a>

> Reactive Streams를 구현한 비동기 데이터 스트림 처리 지원\
> 연산들을 조합하여 가독성 증대

![](https://velog.velcdn.com/images/van1164/post/182885fa-20c9-459b-9fa1-1675dfac40ec/image.png)

## Publisher와 Subscriber <a href="#publisher-subscriber" id="publisher-subscriber"></a>

### Publisher <a href="#publisher" id="publisher"></a>

> Publisher의 구현체에는 Flux와 Mono가 있다.

#### Mono <a href="#mono" id="mono"></a>

> 0부터 1개의 item을 subscriber에게 전달한다.\
> `subscriber`에게 **onComplete**, **onError** signal을 전달하면 연결이 종료된다.\
> **onNext**가 호출되면 곧바로 onComplete 이벤트가 전달된다.

#### Flux <a href="#flux" id="flux"></a>

> 0부터 n개의 item을 subscriber에게 전달한다.\
> `subscriber`에게 **onComplete**, **onError** signal을 전달하면 연결이 종료된다.

#### ❓ Flux도 하나의 값을 보낼 수 있는데, 그럼 Mono는 왜 필요할까? <a href="#flux-mono" id="flux-mono"></a>

물론, Flux가 Mono의 역할을 대부분 대체할 수 있다고 생각한다. 그럼에도 Mono가 필요한 이유는 다음과 같다.

*   **반드시 하나의 값을 필요로하는 경우**

    유저가 작성한 게시글의 숫자\
    http 응답 객체
* **값이 하나이므로 onNext 이후 바로 onComplete를 호출하면 되기때문에 구현이 간단하다.**
* **Subscriber도 최대 1개의 item이 전달된다는 것이 보장되므로 더 간결하게 코드작성이 가능.**

***

### Subscriber <a href="#subscriber" id="subscriber"></a>

> Flux와 Mono 객체를 생성만 하면 아무 일도 일어나지 않는다.\
> 실제로 Flux와 Mono를 구독을 해야 데이터를 읽어올 수 있다.\
> Flux와 Mono를 구독하는 subscribe 메서드는 여러가지가 있는데 하나하나 살펴보자.

```
subscribe(); //시퀀스를 구독하고 트리거한다.

subscribe(Consumer<? super T> consumer); //방출된 값 각각으로 어떤 행동을 한다.

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer); //에러가 발생할 때는 별도의 행동을 한다.

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer,
          Runnable completeConsumer); //시퀀스가 완료되었을 때는 또 다른 행동을 한다.

subscribe(Consumer<? super T> consumer,
          Consumer<? super Throwable> errorConsumer,
          Runnable completeConsumer,
          Context initialContext);
// upStream에 전달할 context
```

> **`Consumer<? super Subscription> subscriptionConsumer)`**\
> 이런 파라미터가 있는 subscribe도 있었지만 최근 **Deprecated**되었다.

#### 예시 <a href="#undefined" id="undefined"></a>

**자바**

```
Flux.fromIterable(List.of(1,2,3,4))
	.subscribe(new Consumer<Integer>(){
    	@Override
        public void accept(Integer integer){}
    },
    new Consumer<Throwable>(){}, //위와 같이 accept를 override
    new Runnable() {}, //void run()을 override해서 사용가능.
    Context.empty()
```

**코틀린**

```
Flux.range(1, 10)
    .subscribe(
        object : Consumer<Int> {
            override fun accept(int : Int){
                println(int)
            }
        },
        object : Consumer<Throwable>{
            override fun accept(t: Throwable) {
                println("error")
            }
        },
        object : Runnable{
            override fun run() {
                println("run")
            }
        },
        Context.empty()
    )
```

***

#### 람다식으로 더 보기 좋게 처리 가능 <a href="#undefined" id="undefined"></a>

**자바**

```
Flux.fromIterable(List.of(1,2,3,4))
	.subscribe(value -> {},
    			error->{},
                () ->{},
                Context.empty()
             )
```

**코틀린**

```
Flux.range(1, 10)
    .subscribe(
        { int -> println(int) },
        { error -> println("error") },
        Runnable { println("run") },
        Context.empty()
    )
```

***

#### BaseSubscriber를 통해 미리 선언도 가능 <a href="#basesubscriber" id="basesubscriber"></a>

**자바**

```
var subScriber = new BaseSubscriber<Integer>() {

      @Override
      public void hookOnSubscribe(Subscription subscription) {
        request(1);
      }

      @Override
      public void hookOnNext(Integer integer) {
        System.out.println("Cancelling after having received " + integer);
        cancel();
      }
    }

Flux.range(1, 10)
    .doOnRequest(r -> System.out.println("request of " + r))
    .subscribe(subScriber);
```

**코틀린**

```
val subscriber = object : BaseSubscriber<Int>() {
    override fun hookOnComplete() {
        logger.info("complete")
    }

    public override fun hookOnNext(integer: Int) {
        println("Cancelling after having received $integer")
        cancel()
    }
}

Flux.range(1, 10)
    .doOnRequest { r: Long -> println("request of $r") }
    .subscribe(subscriber)
```

***

참고자료

[https://sjh836.tistory.com/185](https://sjh836.tistory.com/185) \[빨간색코딩:티스토리]\
[https://godekdls.github.io/Reactor%20Core/reactorcorefeatures/](https://godekdls.github.io/Reactor%20Core/reactorcorefeatures/)\
[https://devsh.tistory.com/entry/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EB%A6%AC%EC%95%A1%ED%84%B0-%EA%B3%A0%EA%B8%89-%ED%99%9C%EC%9A%A9](https://devsh.tistory.com/entry/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%EB%A6%AC%EC%95%A1%ED%84%B0-%EA%B3%A0%EA%B8%89-%ED%99%9C%EC%9A%A9)
