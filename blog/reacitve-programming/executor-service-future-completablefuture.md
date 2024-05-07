# Executor Service와 Future, 그리고 CompletableFuture

## Executor Service <a href="#executor-service" id="executor-service"></a>

> 😎 **쓰레드 풀**을 이용해서 자원을 효율적으로 관리해서 **비동기 작업**을 실행 및 관리를 도와주는 서비스

### ExecutorService의 생성 <a href="#executorservice" id="executorservice"></a>

#### newSingleThreadExecutor <a href="#newsinglethreadexecutor" id="newsinglethreadexecutor"></a>

> 한 번에 **하나의 작업**만 실행하도록 단일 쓰레드로 구성

#### newFixedThreadPool <a href="#newfixedthreadpool" id="newfixedthreadpool"></a>

> 쓰레드 **개수를 지정**해주고 그 만큼의 쓰레드만 생성

#### newCachedThreadPool <a href="#newcachedthreadpool" id="newcachedthreadpool"></a>

> 사용가능한 쓰레드가 없으면 **새로 생성**하고, 일정시간 사용하지 않으면 삭제

#### newScheduledThreadPool <a href="#newscheduledthreadpool" id="newscheduledthreadpool"></a>

> **스케줄링 기능**을 갖춘 고정 크기의 쓰레드풀을 생성.

#### newWorkStealingPool <a href="#newworkstealingpool" id="newworkstealingpool"></a>

> work steal알고리즘을 사용하는 ForkJoinPool생성

***

## Future <a href="#future" id="future"></a>

### Future란? <a href="#future" id="future"></a>

> ❓ **비동기적 연산**의 처리 결과를 표현하기 위해 사용된다. 비동기 처리가 완료되었는지 확인하고, 처리 완료를 기다리고, 처리 결과를 반환하는 메서드를 제공한다.\
> Future를 이용하면 멀티 스레드 환경에서 처리된 어떤 데이터를 **다른 스레드에 전달**할 수 있으며, Future는 내부적으로 **Thread-Safe** 하게 구현되어 있기 때문에 synchronized block(동기화 블록)을 사용하지 않아도 된다.\
> Future 객체는 작업이 완료될 때까지 기다렸다가 최종 결과를 얻는 데 사용하며, 때문에 **지연 완료(pending completion) 객체**라고도 한다.

![](https://velog.velcdn.com/images/van1164/post/922b514a-6299-4464-9610-efc54ca591a0/image.png)

**Future를 반환받은 후에 Future인터페이스를 통해 isDone(), isCancelled() 등을 통해 작업상태를 확인할 수있고 작업이 완료되었다면, get()을 통해 값을 반환받을 수있다.**

### Future인터페이스의 단점 <a href="#future" id="future"></a>

#### 1. cancel을 제외하고 외부에서 future를 컨트롤 할 수 없다. <a href="#id-1-cancel-future" id="id-1-cancel-future"></a>

#### 2. 반환된 결과를 get()을 통해 접근하기 때문에 비동기 처리가 어렵다. <a href="#id-2-get" id="id-2-get"></a>

***

### CompletableFuture의 등장 <a href="#completablefuture" id="completablefuture"></a>

> **Future**를 외부에서 완료시킬 수 있어서 **CompletableFuture**라는 이름을 갖고 있다.\
> **예를 들어,** Future에서는 불가능했던 "**몇 초 이내에 응답이 안 오면 기본값을 반환한다.**" 와 같은 작업이 가능해진 것이다.\
> 즉, **Future**의 진화된 형태로써 **외부에서 작업을 완료**시킬 수 있을 뿐만 아니라 **콜백 등록** 및 **Future 조합** 등이 가능하다는 것이다.

### 연산자 <a href="#undefined" id="undefined"></a>

#### 1. runAsync <a href="#id-1-runasync" id="id-1-runasync"></a>

```
public static CompletableFuture<Void> runAsync(Runnable runnable) {}
```

**값을 반환하지 않는 비동기 작업 실행**

***

#### 2. supplyAsync <a href="#id-2-supplyasync" id="id-2-supplyasync"></a>

```
public static<U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {}
```

**값을 반환하는 비동기 작업 실행**

***

#### 3. complete <a href="#id-3-complete" id="id-3-complete"></a>

```
CompletableFuture<Integer> future = new CompletabeFuture();
future.complete(1);
```

**CompletableFuture가 완료되지 않았다면 주어진 값으로 채움.**

***

#### 4. isCompletedExceptionally <a href="#id-4-iscompletedexceptionally" id="id-4-iscompletedexceptionally"></a>

```
future.isCompletedExceptionally();
```

**CompletableFuture가 예외로 인해 complete된것 인지 확인 가능.**

> **Future**에서는 **isDone**과 **isCanceled**만 존재했기때문에 예외로인해 종료된 것을 알 수없었다.

***

#### 5. allOf <a href="#id-5-allof" id="id-5-allof"></a>

> 여러 **completableFuture**를 모아서 하나의 **completableFuture**로 변환할 수있다.\
> **모든 completableFuture가 완료되어야 done**\
> Void를 반환

***

#### 6. anyOf <a href="#id-6-anyof" id="id-6-anyof"></a>

> 여러 **completableFuture**를 모아서 하나의 **completableFuture**로 변환할 수있다.\
> **하나의 completableFuture만 완료되면 done**상태로 변경 후 완료된 future값 반환

***

> 💘 다음에는 실사용 코드를 정리해볼 예정이다.
