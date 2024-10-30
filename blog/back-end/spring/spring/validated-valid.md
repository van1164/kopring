# @Validated와 @Valid를 통한 유효성검사

## 유효성 검증

다음과 같은 **`TripDto`**를 RequestBody로 받는 컨틀롤러가 있다고 가정해보자.

```java
@PostMapping("")
public CustomResponseBody createTrip(@RequestBody TripDto dto) {
}
```

**`TripDto`** 는 다음과 같다.

```java
public class TripDto {
    private String tripName;
    private Boolean isDomestic;
    private LocalDate startDate;
    private LocalDate endDate;
}
```

이 때 TripName은 50글자 이하여야하고, startDate가 endDate보다걸빠른 날짜여야한다는걸검증하려면 어떻게 해야 할까? 또한 각각의 값도 null이 아니어야 한다.

우선 직접 검증할 경우 dto를 받아와서 다음과 같이 처리해야 할 것이다.

### 매번 유효성 검사

```java
@PostMapping("")
public CustomResponseBody createTrip(@RequestBody TripDto dto) {
    if (dto.getTripName().length()<=50){}
    if (dto.getStartDate().isBefore(dto.getEndDate()){}
    if (dto.getTripName() == null || dto.getTripName().isBlank() ||
        dto.getIsDomestic() == null ||
        dto.getStartDate() == null || dto.getEndDate() == null) {}
}
```

이렇게 검증하면 이 dto를 사용하는 함수마다 이러한 검증을 진행해주어야 한다. 이는 코드의 가독성을 떨어뜨릴 뿐아니라 중복된코드를 계속 작성해야 한다. 그렇다면 이 부분을 함수로 빼면 어떨까?

### 함수로 분리

```java
@PostMapping("")
public CustomResponseBody createTrip(@RequestBody TripDto dto) {
    if (tripDtoValid(dto)){}
}

public Boolean tripDtoValid(TripDto dto){
    if (dto.getTripName().length()<=50){}
    if (dto.getStartDate().isBefore(dto.getEndDate()){}
    if (dto.getTripName() == null || dto.getTripName().isBlank() ||
        dto.getIsDomestic() == null ||
        dto.getStartDate() == null || dto.getEndDate() == null) {}
}
```

이렇게 하면 반복된 코드를 줄일 수 있다. 하지만 함수를 통한 검증은 dto를 사용하는 함수마다 사용해야 하는 것은 마찬가지이다.&#x20;

또한 가장 중요한 것은 우리가 검증이 잘 구현되어 있다는 확신이 없다.

### @Valid와 @Validated 를 통한 검증

@Valid와 @Validated는 java와 Spring에서 제공하는 유효성 검증 라이브러리이다. 라이브러리의 장점은 많은 사용자들이 사용하고 오류를 고쳐나갔기 때문에 믿고 사용할 수 있다는 점이다.



#### dependencies

```gradle
dependencies {
//
    implementation 'org.springframework.boot:spring-boot-starter-validation:2.7.4'
}
```

#### @Valid&#x20;

```java
@PostMapping("")
public CustomResponseBody futureTrip(
    @RequestBody 
    @Valid TripDto dto) { // @Valid 를 선언한 클래스는 검증을 진행한다.
}
```

#### Validation 어노테이션

```java
@ValidDateRange     //커스텀 Valid 어노테이션 시작이 종료보다 빠른지 검사
public class TripDto {
    @NotBlank
    @Size(min = 1, max=50)
    private String tripName;
    @NotNull
    private Boolean isDomestic;
    private LocalDate startDate;
    private LocalDate endDate;
}
```

이렇게 dto에 어노테이션을 통해 가독성 좋게 검증이 가능하다.&#x20;

또한, @ValidDateRange 처럼 커스텀해서 구현도 가능하다. valid 어노테이션을 커스텀하는 방법은 다음 포스팅에서 다룰 예정이다.

**만약, 클래스안에 클래스가 있다면 @Valid 어노테이션을 추가해주어야한다.**

```java
public class TripDto {
    @Valid
    private newData newData;
}
```





## **그렇다면@Valid와 @Validated는 무슨 차이가 있을까?**

## @Valid와 @Validated 차이

@Valid와 @Validated는 거의 동일한 기능을 합니다. @Validated가 @Valid를 변형한 것이기 때문이다.&#x20;

둘의 차이는  크게 두가지이다.

### 1.  그룹 유효성 검사의 지원

@Valid는 그룹 유효성 검사를 **지원하지 않고**, @Validated는 그룹 유효성 검사를 **지원한다.**

#### 그룹 유효성 검사란?

하나의 DTO에서도 상황에 따라서 유효성 검사를 다르게 해야 할 경우가 있다. 예를 들어 **과거의 여행에 대한 기록 서비스**와 **앞으로의 여행 계획을 세우는 서비스** 2가지의 서비스에서 **`TripDto`** 를 사용한다고 해보자.&#x20;

과거 여행에 대한 기록 서비스에서 startDate와 endDate는 현재보다 과거여야 한다.

앞으로의 여행 계획을 세우는 서비스에서는 startDate와 endDate는 현재보다 미래여야 한다. 이를 검증하기 위해서는 같은 엔티티에 다른 검증을 구현해야하는데, 이를 그룹으로 묶어서 유효성검사를 지원하는 방법이 **그룹 유효성** 검사이다.

```java
public class TripDto {
    private String tripName;
    private Boolean isDomestic;
    @Past(groups = PastValidation.class)
    @Future(groups = FutureValidation.class)
    private LocalDate startDate;

    @Past(groups = PastValidation.class)
    @Future(groups = FutureValidation.class)
    private LocalDate endDate;
    
    interface PastValidation{}
    interface FutureValidation{}
    
}
```

#### 과거 계획 컨트롤러

```java
@PostMapping("")
public CustomResponseBody futureTrip(
    @RequestBody 
    @Validated(PastValidation.class) TripDto dto) {
}
```

#### 미래 계획 컨트롤러

```java
@PostMapping("")
public CustomResponseBody futureTrip(
    @RequestBody 
    @Validated(FutureValidation.class) TripDto dto) {
}
```

이렇게 Validation에 대한 그룹을 나누어서 진행이 가능하다.



### 2. 발생시키는 예외

@Valid는 **`MethodArgumentNotValidException`**

@Validated 는**`ConstraintViolationException`**

로 발생시키는 Exception의 차이가 있다.

따라서 아래와 같이 ExceptionHandler의 두개의 클래스를 등록해서 같이 처리도 가능하고 따로 등록해도 상관없다.

{% code fullWidth="true" %}
```java
@ExceptionHandler(value = {MethodArgumentNotValidException.class,ConstraintViolationException.class})
public ResponseEntity<CustomResponseBody> validateError(Exception e){
    return createExceptionResponse(HttpStatus.BAD_REQUEST,bindingResult);
}
```
{% endcode %}



## 참고 사이트

{% embed url="https://adjh54.tistory.com/77" %}

{% embed url="https://sanghye.tistory.com/36" %}

{% embed url="https://medium.com/sjk5766/valid-vs-validated-%EC%A0%95%EB%A6%AC-5665043cd64b" %}

{% embed url="https://mangkyu.tistory.com/174" %}



