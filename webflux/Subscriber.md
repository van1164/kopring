## Subscriber
> Flux와 Mono 객체를 생성만 하면 아무 일도 일어나지 않는다. <br>
실제로 Flux와 Mono를 구독을 해야 데이터를 읽어올 수 있다. <br>
Flux와 Mono를 구독하는 subscribe 메서드는 여러가지가 있는데 하나하나 살펴보자.<br>

 <br>
 
```kotlin
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

> **`
Consumer<? super Subscription> subscriptionConsumer)
`**
이런 파라미터가 있는 subscribe도 있었지만 최근 **Deprecated**되었다.

### 모든 기능을 다 Override 했을 때 예시
```kotlin
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
#### 람다식으로 더 보기 좋게 처리 가능

```kotlin
Flux.range(1, 10)
    .subscribe(
        { int -> println(int) },
        { error -> println("error") },
        Runnable { println("run") },
        Context.empty()
    )
```
#### BaseSubscriber를 통해 미리 선언도 가능
``` kotlin
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
