# 데이터베이스 격리수준과 동시성(feat. Locking과 MVCC 각각 관점에서)

## 동시성 문제 <a href="#undefined" id="undefined"></a>

> 트랜잭션은 원자성, 일관성, 격리성, 지속성을 보장해야 한다.\
> 이 중 격리성은 동시에 실행 되는 트랜잭션들이 서로에게 영향을 미치지 않아도록 격리하는 것을 말한다.
>
> 격리성을 완벽하게 보장하려면 같은 테이블에 대해 실행되는 트랜잭션을 모두 차례대로 실행해야한다. 하지만 이렇게 하면 애플리케이션의 성능이 매우 느려진다.\
> 따라서, 격리성을 보장하면서 동시에 요청을 처리할 수 있도록 적절히 처리하는 것이 필요하다.

### Locking과 MVCC <a href="#locking-mvcc" id="locking-mvcc"></a>

구현된 데이터베이스에 따라서 동시성을 제어하기 위한 방식으로 `MVCC`와 `Locking` 두가지를 사용한다.\


<figure><img src="https://velog.velcdn.com/images/van1164/post/ae706dd9-d09e-4876-b86e-ae72490702a0/image.png" alt=""><figcaption></figcaption></figure>

#### Locking <a href="#locking" id="locking"></a>

Locking은 다른 트랜잭션에서 접근하지 못하도록 락을 걸어서 동시에 여러 트랜잭션이 데이터를 읽거나 쓸 때 데이터의 일관성을 보장하기 위한 전통적인 방법이다.

#### MVCC(Multi-Version Concurrency Control) <a href="#mvccmulti-version-concurrency-control" id="mvccmulti-version-concurrency-control"></a>

각 트랜잭션이 독립적인 데이터 스냅샷을 사용하여 작업할 수 있게 하여 동시성을 제어하기 위해 도입된 기술이다. 일반적으로 락을 걸어서 제어하는 Locking에 비해 속도가 매우 빠르다.

***

### 동시성 문제가 발생하는 경우 <a href="#undefined" id="undefined"></a>

다음과 같은 `Trip` 엔티티가 있다고 해보자.

```kotlin
@Entity
data class(
	@Id
    val id : Long?,
    @Column
    val name : String,
    @Column
    val like : Int
)
```

이때, 두 클라이언트가 동시에 좋아요를 누르면 어떻게 될까?\


<figure><img src="https://velog.velcdn.com/images/van1164/post/f5eef569-8110-4ba5-9239-2ac1b9c48f32/image.png" alt=""><figcaption></figcaption></figure>

아무런 처리가 되어있지 않다면 처음 trip을 조회할 때 like가 0이었기 때문에 두 코드 모두 like를 1로 업데이트하는 쿼리가 실행될 것이다. 이렇게 되면 어떤 쿼리를 먼저 실행하던지 상관없이 like가 1로 업데이트되는 문제가 발생한다.

### 동시 요청에서 생기는 문제들 <a href="#undefined" id="undefined"></a>

**❌이해하기 쉽게 kotlin 코드를 첨가하였지만, 1차캐시같은건 고려하지 않은 코드입니다!**

*   #### Dirty Read <a href="#dirty-read" id="dirty-read"></a>

    커밋하지 않은 데이터를 조회

    > 아래 그림 처럼 Client B가 수정하고 있는 중간에 Client A 가 데이터 조회하는 문제

![](https://velog.velcdn.com/images/van1164/post/2ddeefb4-bf4d-4bdd-9a27-63d4905b4af5/image.png)

***

*   #### Non-Repeatable Read <a href="#non-repeatable-read" id="non-repeatable-read"></a>

    한 트랜잭션에서 같은 row에 대해서 다른 값이 조회됨

> 아래 그림처럼 Client A가 두번 같은 데이터를 조회했는데 중간에 Client B가 수정해서 값이 다른 문제

![](https://velog.velcdn.com/images/van1164/post/181c9502-7d93-44c9-bf73-3f743fe2260c/image.png)

***

*   #### Phantom Read <a href="#phantom-read" id="phantom-read"></a>

    조회했을 때 결과 집합이 달라짐.

    > 아래 그림처럼 client A가 2번 값을 조회했지만 두번째 조회시 새로운 값이 추가된 문제

![](https://velog.velcdn.com/images/van1164/post/1ef1ca46-685e-44e3-854c-ca0801ffb25a/image.png)

***

## 트랜잭션 격리수준 <a href="#undefined" id="undefined"></a>

> 모든 트랜잭션을 차례대로 실행하는 것은 성능이 매우 안좋아지기 때문에 ANSI 표준\
> 은 격리 수준을 4단계로 나누어 정의했다. 격리 수준이 낮을 수록 동시성이 증가하지만 문제가 발생한다.

* READ UNCOMMITED (가장 낮은 단계)
* READ COMMITED
* REPEATABLE READ
* SERIALZABLE (가장 높은 단계)

#### 트랜잭션 격리수준에 따른 문제점 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/04cd156f-44e3-4713-8cf3-2959b67bd743/image.png)

### 1. READ UNCOMMITED <a href="#id-1-read-uncommited" id="id-1-read-uncommited"></a>

가장 낮은 단계의 격리 수준이다. 이름 그대로 커밋되지 않은 내용을 읽을 수 있는 격리단계이다. 위에서 작성한 모든 문제점이 발생하는 단계인데, 특히나 격리단계 이름에 맞게 `Dirty Read`를 허용한다.

***

### 2. READ COMMITED <a href="#id-2-read-commited" id="id-2-read-commited"></a>

이 격리단계에서는 커밋된 데이터만 읽을 수 있다. 즉, `Dirty Read`를 허용하지 않는다. 하지만 `Non-Repeatable Read`를 허용한다. 따라서 이 격리단계에서는 한 트랜잭션에서도 같은 값을 조회해도 다른 트랜잭션에서 업데이트했다면 업데이트된 값이 조회될 수 있다.

#### **lock을 사용해 구현한 경우**

> `lock`을 통해 구현된 경우 수정할 때 쓰기 Lock을 걸고 조회할 때 읽기 Lock을 걸어서 `Dirty Read`를 막는다. 하지만 조회하는 두 과정 사이에 업데이트를 한다면 다시 읽었을 때 같은 값이 나오지 않는 `Non-Repeatable Read` 문제가 발생하며 이를 허용한다.

![](https://velog.velcdn.com/images/van1164/post/152bbf80-46df-49fc-b5a7-72cfbb016dba/image.png)

#### **MVCC를 사용할 경우**

> `MVCC`를 통해 관리되는 데이터베이스의 경우 직전까지 커밋된 데이터베이스의 Snapshot을 사용하는데, 다른 트랜잭션이 업데이트 후 커밋을 완료하면 그 시점부터는 커밋된 Snapshot을 사용하기 때문에 이 격리단계에서는 업데이트를 허용한다. 따라서 다시 읽었을 때 같은 값이 나오지 않는 `Non-Repeatable Read`가 허용된다.

![](https://velog.velcdn.com/images/van1164/post/a5b98ee4-29d8-4b49-b054-1345a2fa89a3/image.png)

***

### 3. REPEATABLE READ <a href="#id-3-repeatable-read" id="id-3-repeatable-read"></a>

이 격리수준의 이름 그대로 반복해서 조회해도 처음 조회했던 내용에서 변경되지 않는다.&#x20;

하지만 `Phantom Read`는 여전히 발생한다.

`Phantom Read`는 주로 `lock`을 사용할 경우 발생하며, `MVCC`를 사용할 때에는 **딱 한가지 경우** 발생한다.

**lock을 사용해 구현한 경우**

> lock을 사용할 경우 조회한 row에 대해서 트랜잭션이 끝날 떄까지 lock을 걸기 때문에 새로운 업데이트를 하지 못해서 `Non-Repeatable Read`를 막는다. 하지만 조회한 row가 아닌 row에 대해서는 lock을 걸지 않았고, 따라서 새로 row가 추가되는 것은 막지 못한다. 이 때문에 조회시에 새로운 값이 추가되는 `Phantom Read`가 발생하며 이를 허용한다.

![](https://velog.velcdn.com/images/van1164/post/64628cf7-1a15-4fbc-b922-5c9a0ae31c7e/image.png)

**MVCC를 사용할 경우**

> MVCC를 사용할 경우 SnapShot을 사용하되 자신보다 낮은 트랜잭션 번호를 갖는 트랜잭션에 대해서만 사용한다. 따라서 다른 트랜잭션이 업데이트를 해도 상관없이 동일한 내용을 조회하게 된다. 이후의 트랜잭션이 행한 작업은 무시하기 때문에 새로운 값을 추가할 때 발생하는 `Phantom Read`가 발생하지 않는다.
>
> 다만 `SELECT … FOR UPDATE` 명령을 사용하는 딱 한경우 발생한다. 이 구문을 사용해서 조회할 경우 조회한 row에 lock을 거는데 snapshot에는 lock을 걸 수 없기 때문에 실제 테이블을 조회한다. 이는 lock을 사용한 구현과 동일하게 `Phantom Read`가 발생하며 이를 허용한다.

![](https://velog.velcdn.com/images/van1164/post/e637f727-630f-442f-8efc-9278691cf9a3/image.png)

***

### 4. SERIALIZABLE <a href="#id-4-serializable" id="id-4-serializable"></a>

이 격리 단계는 대부분의 데이터베이스에서 모두 lock방식으로 동작하며 하나의 트랜잭션이 완료될 때까지 접근하는 모든 데이터에 Shared Lock이 걸리기 때문에 추가/수정/삭제 를 다른 트랜잭션에서 할 수없다. (방식은 데이터베이스 마다 다르다.)

### 참고 <a href="#undefined" id="undefined"></a>

**Locking**

{% embed url="https://doooyeon.github.io/2018/09/29/transaction-isolation-level.html" %}

{% embed url="https://mangkyu.tistory.com/298" %}

**MVCC**

{% embed url="https://mangkyu.tistory.com/299" %}

{% embed url="https://amaran-th.github.io/%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4/[MySQL]%20%ED%8A%B8%EB%9E%9C%EC%9E%AD%EC%85%98%20%EA%B2%A9%EB%A6%AC%EC%88%98%EC%A4%80%EA%B3%BC%20MVCC/#innodb%EC%9D%98-repeatable-read%EC%97%90%EC%84%9C%EB%8A%94-phantom-read%EA%B0%80-%EB%B0%9C%EC%83%9D%ED%95%98%EC%A7%80-%EC%95%8A%EB%8A%94%EB%8B%A4" %}
