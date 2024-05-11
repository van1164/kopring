# N+1문제에 대한 생각 (feat. 이제 발생하지 않게 업데이트 되었나?)

## N+1문제

N+1문제는JPA를 통해 연관관계를 가지는 데이터를 조회하는 상황에서 마주치게 되는 문제이다.

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

### JpaRepository를   사용해서 Trip을 id로조회해보자

```java
tripRepository.findById(id);
```

<figure><img src=".gitbook/assets/image.png" alt=""><figcaption></figcaption></figure>

예상과 달리 JpaRepository를 통한 조회에서는 join을 통해 한번에 모든 연관된 데이터를 불러오고 있었다.&#x20;

### EntityManger를 통해 조회해보자.

```kotlin
@Test
fun contextLoads() {
    val trip = em.createQuery("select t from Trip t").resultList
    println(trip.first())
}
```

<figure><img src=".gitbook/assets/image (1).png" alt=""><figcaption></figcaption></figure>

이번에도 연관되어 있는 모든 데이터에 대한 쿼리문이 아닌 한번에 쿼리를 보냈다.

**`entity Manager`** 를 clear해도 결과는 똑같았다.

### 예상한 시나리오
