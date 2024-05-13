# @Transactional에서 readOnly = true를 하면 무슨 일이 벌어질까



### 데이터베이스에서값을 읽기만 할때에는 Transactional에서 readOnly = true로 지정하면 좋다고 한다.&#x20;

### 그래서 이렇게 하면 무엇이 좋고 왜 써야 하는지 학습한 내용을 정리하였다.



앞서서 findById와 같은 방식으로 엔티티를 조회하면 1차캐시와 스냅샷에 저장을 한다. 이를 통해 변경을 감지하여 업데이트를 자동으로 수행하기 때문에 개발자가 update쿼리를 수행하지 않아도 된다.

### 만약 조회만 하는 함수라면?

**하지만** 만약 값을 업데이트없이 조회만을 위한 함수에 경우에는 어떨까?? 1차캐시와 스냅샷에 조회한 데이터를 저장해놓고 **전혀 사용하지 않게 된다.** 그렇게 되면 조회할 때 1차 캐시와 스냅샷에 저장하는 **시간과 메모리를 낭비**하게 된다. **특히, 스냅샷을 비교하는 과정은 매우 무거운 로직이다.** 이를 방지하기 위해 **readOnly**속성을 사용하게 된다.

### readOnly 를 true로 하면?

`readOnly` 속성을 `true`로 지정하면 플러쉬 모드를 `MANUAL`로 설정한다. 이렇게 하면 flush를 강제로 호출하지 않으면 절대 일어나지 않는다. flush를 하지 않기 때문에 `Insert`, `Update`, `Delete` 쿼리는 발생하지 않는다.

**대신** flush를 호출하였을 때 발생하는 스냅샷과 비교하는 로직이 수행되지 않는다. 무거운 로직을 실행하지 않기 때문에 성능을 향상시킬 수 있는 것이다.

하지만, 1차캐시와 스냅샷에 저장하지 않는 것은 아니다. 따라서 저장해놓고 사용하지 않는 것은 똑같다. 그렇다면 저장도 하지 않으려면 어떻게 해야 할까?

### 읽기 전용 쿼리 힌트

다음과 같이 읽기 전용 쿼리힌트를 넣어주면 1차캐시와 스냅샷에도 저장하지 않는다.

#### JpaRepository

```kotlin
@QueryHints(value = [QueryHint(name = "org.hibernate.readOnly", value = "true")])
override fun findById(id : Long): Optional<Trip>
```

#### EntityManager

```kotlin
em.createQuery("select t from Trip t").setHint("org.hibernate.readOnly",true)
```

### 결론

@Transactional(readOnly = true)를 지정하면 flush를 하지 않아서 스냅샷과 1차캐시의 내용을 비교하는 작업을 하지 않어서 성능을 향상 시킨다.

QueryHint의 readOnly를 true로 지정하여 쿼리를 구성하면 1차캐시와 스냅샷에 저장하지 않기 때문에 메모리상의 이점을 가질 수 있다.
