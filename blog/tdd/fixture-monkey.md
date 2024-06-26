# Fixture Monkey 적용기

![](https://velog.velcdn.com/images/van1164/post/49fbb70b-5466-45eb-ad70-873583190801/image.png)

### MockUp(Test Fixture) 만들기 <a href="#mockuptest-fixture" id="mockuptest-fixture"></a>

토비님의 말씀대로 TDD를 열심히 실천하고 있는데, 불편한 점이 있었다. 바로 **MockUp** 데이터를 만드는 작업이다. 토비의 스프링 책에서는 **Test Fixture**라고 한다.

특히나 의존하고 있는 관계가 많을 수록 만들어야 하나의 Fixture를 만들기 위해 만들어야 할 객체들이 많았다.

#### Fixture 만들기 예시 <a href="#fixture" id="fixture"></a>

```java
Accommodation accommodation = Accommodation.builder()
                                   .name("숙소 이름")
                                   .build();
Room room = Room.builder()
                .roomTypeName("이름")
                .roomType(RoomType.DOUBLE)
                .roomPrice(1000)
                .roomDefaultGuest(4)
                .accommodation(accommodation)
                .build();

roomTestRepository.save(room);
```

이렇게 해야 하나의 **Fixture**를 만들 수 있으며 여러개를 만드려면 코드도 길어질 뿐아니라 더 좋은 테스트를 위해 객체의 내용도 바꿔줘야 하는데 쉽지가 않다.

### Fixture Monkey <a href="#fixture-monkey" id="fixture-monkey"></a>

Naver에서 만든 오픈소스 라이브러리로 테스트 객체를 자동으로 만들어준다. 특히나 원하면 Validation 설정값에 맞는 데이터로만 생성할 수도 있고, 랜덤한 값으로 엣지케이스도 테스트해볼 수 있다.

#### 의존성 추가 <a href="#undefined" id="undefined"></a>

```groovy
testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter:1.0.19")
testImplementation('com.navercorp.fixturemonkey:fixture-monkey-jakarta-validation:1.0.19')
```

#### 인스턴스 생성 <a href="#undefined" id="undefined"></a>

```java
    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .plugin(new JakartaValidationPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build();
```

**objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)**\
lombok을 사용해서 생성자를 만들었다면 위와같은 옵션을 추가해주어야 한다.

**plugin(new JakartaValidationPlugin())**\
Validation 설정한 값만 생성 받고 싶으면 다음과 같은 옵션을 추가해주어야 한다.

#### 사용 예제 <a href="#undefined" id="undefined"></a>

* 하나의 Fixture 생성

```java
Room room = fixtureMonkey.giveMeOne(Room.class);
```

* 여러 Fixture 한번에

```java
List<Room> roomList = fixtureMonkey.giveMe(Room.class, 3); // 3개 생성
```

* Builder를 활용해서 특정값 고정해서 받기

```java
List<Room> roomList = fixtureMonkey.giveMeBuilder(Room.class)
                                     .setNull("id")
                                     .sampleList(10);
```

#### JUnit에서 사용해보기 <a href="#junit" id="junit"></a>

```java
@SpringBootTest
public class OrderServiceTest {
    //생략

    FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .plugin(new JakartaValidationPlugin())
            .objectIntrospector(FieldReflectionArbitraryIntrospector.INSTANCE)
            .build();

    List<Room> roomList;

    OrderDTO orderDTO;

    @BeforeEach
    public void beforeEach() {
        List<Room> insertList = fixtureMonkey.giveMeBuilder(Room.class).setNull("accommodation").setNull("id").sampleList(10);
        System.out.println(insertList);
        for(Room room : insertList){
            System.out.println(room);
        }
        roomList = roomTestRepository.saveAll(insertList);
        Integer totalPrice = roomList.stream().mapToInt(Room::getRoomPrice).sum();
        List<Long> idList = roomList.stream().map(Room::getId).toList();
        List<OrderRequest> orderRequestList = idList.stream().map(id->{
            return fixtureMonkey.giveMeBuilder(OrderRequest.class).set("id",id).sample();
        }).toList();

        orderDTO = new OrderDTO(orderRequestList,totalPrice);
    }

    @RepeatedTest(5)
    @Rollback
    public void payTest() throws OrderException.TotalPriceNotEqualsException, OrderException.DeletedRoom {
        //given

        System.out.println(orderDTO);

        //when
        Order result = orderService.pay(orderDTO,1L);
        Order order = orderRepository.findById(result.getId()).orElseGet(null);
        List<DoneRoom> doneRoomList = doneRoomRepository.findAllByOrderId(order.getId());

        //then

        assertNotNull(order);
        assertEquals(result, order);
        assertEquals(roomList.size(), doneRoomList.size());

        for(int i =0; i< roomList.size(); i++){
            assertEquals(roomList.get(i).getRoomType(),doneRoomList.get(i).getRoomType());
        }

    }
```

랜덤으로 값이 결정되다보니 @RepeatedTest를 통해서 여러번 시도해서 테스트를 할 수있다.\
자동 생성된 값을 출력해보면 아래 처럼 정말 랜덤으로 제공되었다.

![](https://velog.velcdn.com/images/van1164/post/efed9c47-c243-4fb5-a7e3-09163a516211/image.png)

### 테스트 결과 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/53529754-3c23-4e86-a331-2c17016df516/image.png)

### 결론 <a href="#undefined" id="undefined"></a>

지금까지 테스트를 할 때 여러 객체를 만드려면 많은 시간이 소요되었다. 거기에 내가 만든 **Fixture**는 내 생각안에서 만들어져서 고정되기 때문에, 오류가 발생할 수 있는 객체가 존재하지 않을까 하는 의구심이 들었다.

단위 테스트를 하고나서도 구현한 코드에 대해 확신을 갖지 못하는 것이 매일 찜찜하였는데, 이 라이브러리를 사용해서 여러 번 반복해서 테스트를 하면 내가 만든 고정된 테스트보다 확신을 가지고 개발에 나아갈 수 있어서 좋았다.

보통 라이브러리는 특수한 경우에 사용하고 다시 찾게 되는 경우가 적은데, Fixture-Monkey는 매 개발시에 필수적으로 사용될 것같다.
