# N+1문제에 대한 생각 (feat. 이제 발생하지 않게 업데이트 되었나?)

## N+1문제

N+1문제는JPA를 통해 연관관계를 가지는 데이터를 조회하는 상황에서 마주치게 되는 문제이다.&#x20;

하나의 엔티티에 N개의 연관된 데이터가 있을 때 조회문이 N+1번 실행되는 문제로 알려져 있다.

다음과 같이  여행에 대한 Entity 클래스가 있다고 해보자.

```java
@Entity
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "trip", fetch = FetchType.EAGER)
    private List<Location> locations;
}
```

### 예상 시나리오

Location이 N개 있다면 총 N+1번의 쿼리가 나가야 한다.

### JpaRepository를   사용해서 전체 Trip을 조회해보자.

```java
tripRepository.findAll();
```

<figure><img src=".gitbook/assets/image (3).png" alt=""><figcaption></figcaption></figure>

예상과 달리 JpaRepository를 통한 조회에서는 여러번의 쿼리 대신 in을   사용해서 한번에 모든 연관된 데이터를 불러오고 있었다.&#x20;

### EntityManger를 통해 조회해보자.

```kotlin
val trip = em.createQuery("select t from Trip t").resultList

```

<figure><img src=".gitbook/assets/image (1).png" alt=""><figcaption></figcaption></figure>

이번에도 연관되어 있는 모든 데이터에 대한 쿼리문이 아닌 한번에 쿼리를 보냈다.

**`entity Manager`** 를 clear해도 결과는 똑같았다.



> N+1  문제의 해결법으로 주로 나오는@BatchSize 를 사용한 것과 같은 결과이다.

## 버전 낮추기

### 스프링부트   2.7.18

<figure><img src=".gitbook/assets/image (2).png" alt=""><figcaption></figcaption></figure>

### 예상한 시나리오
