# 빈 스코프에 대해서

### 빈(Bean) 스코프란 <a href="#bean" id="bean"></a>

빈이 존재할 수 있는 범위를 나타낸다. 기본 스코프는 싱글톤 스코프로 스프링 컨테이너의 시작과 종료까지 유지되는 가장 넓은 범위의 스코프이다. 이 때문에 기본적으로 만든 빈은 컨테이너가 시작되고 종료될 때까지 유지되는 것이다.

### 빈 스코프의 종류 <a href="#undefined" id="undefined"></a>

### 싱글톤과 프로토타입 <a href="#undefined" id="undefined"></a>

#### 1. sigleton 스코프 <a href="#id-1-sigleton" id="id-1-sigleton"></a>

스코프를 지정하지 않으면 기본적으로 싱글톤 스코프로 지정되어서 스프링 컨테이너는 특정 빈에 대한 요청이 들어왔을 때 같은 인스턴스를 반환한다.

#### 2. prototype 스코프 <a href="#id-2-prototype" id="id-2-prototype"></a>

스프링 컨테이너는 프로토타입 빈의 생성과 의존 관계 주입까지만 관여하고, 더는 관리하지 않는 매우 짧은 범위의 스코프이다. 따라서 종료 메서드는 호출되지 않는다.

따라서 스프링컨테이너에 특정 빈을 요청하면 이 시점에서 인스턴스를 생성해서 반환해주고 더 이상 관리하지 않는다.

### 싱글톤 스코프와 프로토타입 스코프 같이 사용시 문제 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/57a75af6-91c8-4ec7-b797-7b185e9bc35b/image.png)

위와 같이 싱글톤 스코프를 가지는 빈에서 프로토타입 스코프를 가지는 빈을 의존하고 있을 때 문제가 발생한다.

프로토타입 스코프를 가지는 빈은 요청할 때마다 새로운 빈이 생성되는 것을 기대하지만, 싱글톤 스코프가 의존하고 있을 때는 싱글톤 빈이 생성되는 시점에 한번 주입된다. 이후에는 싱글톤 빈이 사용하는 프로토타입 빈은 같은 인스턴스이다.

이렇게 사용되면 프로토타입빈으로 설정하였지만, 싱글톤처럼 사용된게 된다.

#### 문제 해결법 : Provider 사용 <a href="#provider" id="provider"></a>

#### 1. ObjectProvider <a href="#id-1-objectprovider" id="id-1-objectprovider"></a>

```java
@Scope("singleton")
static class ClientBean {
	@Autowired
    private final ObjectProvider<PrototypeBean> prototypeBeanProvider;

    public int logic() {
        final PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
        prototypeBean.addCount();
        return prototypeBean.getCount();
    }
}
```

`ObjectProvider`를 사용하면 지정한 빈을 컨테이너에서 찾아준다. 이렇게 사용하면 프로토타입 빈을 사용할 때마다 찾아서 사용하기 때문에, 프로토타입 스코프로 지정하였기 때문에 logic()함수가 실행될 때마다 새로운 인스턴스가 사용된다.

#### 2. JSR-330 Provider <a href="#id-2-jsr-330-provider" id="id-2-jsr-330-provider"></a>

```java
@Scope("singleton")
static class ClientBean {
	@Autowired
    private final Provider<PrototypeBean> prototypeBeanProvider;

    public int logic() {
        final PrototypeBean prototypeBean = prototypeBeanProvider.get();
        prototypeBean.addCount();
        return prototypeBean.getCount();
    }
}
```

`javax.inject:javax.inject` 라이브러리를 추가해서 사용해야하고, 동작은 ObjectProvider와 동일하다. 다만, 자바에서 제공하기 때문에 스프링이 아닌 다른 컨테이너에서도 사용이 가능하다.

### 웹 스코프 <a href="#undefined" id="undefined"></a>

#### 1. request 스코프 <a href="#id-1-request" id="id-1-request"></a>

웹에서 요청이 들어와서 나갈때 까지 유지되는 스코프이다.

#### 2. session 스코프 <a href="#id-2-session" id="id-2-session"></a>

웹 세션이 생성되고 종료될 때까지 유지되는 스코프이다.

#### 3. application 스코프 <a href="#id-3-application" id="id-3-application"></a>

웹의 서블릿 컨텍스트와 같은 범위로 유지되는 스코프이다.

#### 4. websocket 스코프 <a href="#id-4-websocket" id="id-4-websocket"></a>

웹 소켓과 동일한 생명 주기를 가지는 스코프이다.

#### 웹 스코프를 왜 사용할까? <a href="#undefined" id="undefined"></a>

request 스코프를 예로 들면 사용자가 요청을 보냈을 때 각 요청마다의 스코프에 대해서 새로운 인스턴스를 필요로 할 때 사용된다.

예를 들어 요청에 대한 로그를 남겨야 하는 상황을 가정해보자.\
Controller, Service, Repository에서 로거를 주입받아서 사용하였을 때, 로거의 스코프를 싱글톤으로 사용하면 모든 요청에 대해서 같은 로거 인스턴스가 사용될 것이고, 프로토타입을 사용하면 하나의 요청에서 각 주입마다 다른 인스턴스가 사용될 것이다.

하지만 하나의 요청에 대해서 각기 다른 컴포넌트가 같은 로거를 사용하고 싶은 비즈니스 로직이라면 위와 같은 스코프로는 불가능하기 때문에 이런 경우 웹 스코프를 사용하여야 한다고 생각한다.

위 예시에서 `요청` 이라는 단어를 세션, 어플리케이션, 웹소켓 으로만 바꾸면 각각의 웹스코프의 예시가 된다.

### 웹스코프 사용시 오류 <a href="#undefined" id="undefined"></a>

이번에도 request에 대하여 예시를 작성하겠다. 이 또한 모두 `요청`이란 단어를 바꾸면 된다.

request 스코프는 요청이 오지 않으면 인스턴스가 생성되지도 주입되지도 않는다. 따라서 그냥 request 스코프를 사용하는 빈을 의존하는 컴포넌트가 의존이 불가능하여 빌드자체가 되지 않는다.

#### 두가지 해결 방법 <a href="#undefined" id="undefined"></a>

**1. ObjectProvider**

프로토타입 스코프를 사용했을 때와 마찬가지로 사용이 필요한 시점에 빈을 찾아서 불러 올 수 있다. 필요한 시점은 요청을 받았을 경우일 것이기 때문에 적용이 가능하다.

**2. 프록시**

```java
@Scope(value="request",proxyMode= ScopedProxyMode.TARGET_CLASS)
```

jpa에서 사용되는 프록시객체와 비슷한 동작을 한다. 잔짜 객체를 주입하는 것이 아닌 가짜(프록시) 객체를 주입해놓고 실제 사용시에 빈을 찾아서 수행한다.

#### 그럼 프로토타입에서는 프록시로 안되나? <a href="#undefined" id="undefined"></a>

프록시 모드를 넣어서 적용시킬 수는 있겠지만, 원하는 대로 동작하지는 않을 것이다. 이유는 생각해보면 간단하다. 프록시를 사용하면 프로토타입 객체를 사용할 때 진짜 객체를 빈에서 찾아오게 되고 프로토타입 특성상 매번 새로운 인스턴스를 만든다. 따라서 하나의 함수에서도 실행하는 함수마다 각각 다른 인스턴스가 생성된다.

다음과 같은 로거클래스와 실행함수가 있다고 하자\
예시)

#### 프로토타입 + 프록시 클래스 <a href="#undefined" id="undefined"></a>

```kotlin
@Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
class TestLogger {
    var name =  ""

    fun getNameTest(): String {
        println(this)
        return name
    }

    fun setNameTest(name : String){
        println(this)
        this.name = name
    }
}
```

#### 프로토타입 객체 의존 객체 <a href="#undefined" id="undefined"></a>

```kotlin
@RestController
class TestController(
    val testLogger: TestLogger
) {

    @GetMapping("")
    fun test(){
        testLogger.getNameTest()
        testLogger.setNameTest("testName")
    }
```

다음과 같이 하나의 test() 함수에서 프록시 + 프로토타입 객체의 두가지 메소드를 실행하면 어떤 로그가 남을까?

```
com.van1164.jpatest.TripLogger@4b77c42d
com.van1164.jpatest.TripLogger@481cb8e9
```

이렇게 각각의 인스턴스가 다른 것을 알 수있다. 이건 프로토타입 스코프를 사용했을 때 기대하는 동작이 아닐 것이다. **따라서 프록시를 사용해서 프로토타입 스코프의 문제를 해결할 수는 없다.**
