# 멀티모듈 적용기

![](https://velog.velcdn.com/images/van1164/post/986481b2-096c-4ee6-8c3f-cc52a6687028/image.png)

`동영상 스트리밍`에 비해 `라이브스트리밍`은 많은 트래픽을 유발한다. 그래서 모든 서비스들을 하나의 서버에서 운영하는 것보다는 두 도메인을 다른 서버에 구축하고 `라이브스트리밍`의 서버를 좀 더 트래픽을 감당할 수 있도록 구축하는 것이 효과적이라고 생각했다.

그래서 분리를 하려고 보니 `동영상 스트리밍`과 `라이브 스트리밍` 모두 `User` 클래스를 사용하고 있었기 때문에 두 서버가 각각 User를 가지고 있어야 하는 상황이 생겼다. 이걸 이렇게 구현하는게 맞을지 조사를 해보던 중에 **멀티모듈** 방식을 알게 되었습니다.

> 그래서 많은 블로그들을 찾아봤는데 멀티모듈에 대한 이해는 우아한 기술블로그의 아래 글이 도움되었습니다.\
> [https://techblog.woowahan.com/2637/](https://techblog.woowahan.com/2637/)

> 이 포스팅에서는 멀티모듈로 변경하면서 어떤 것을 느꼈고 어떠한 장단점들이 있었는지 작성할 예정입니다. 구현방법이 필요하신 분들은 아래 블로그가 잘 작성되어 있어서 대체하겠습니다.\
> [https://velog.io/@shyeom1023/Kotlin-Spring%EC%9C%BC%EB%A1%9C-%EB%8B%A4%EC%A4%91-%EB%AA%A8%EB%93%88-%EA%B5%AC%EC%84%B1-%EB%B0%A9%EB%B2%95](https://velog.io/@shyeom1023/Kotlin-Spring%EC%9C%BC%EB%A1%9C-%EB%8B%A4%EC%A4%91-%EB%AA%A8%EB%93%88-%EA%B5%AC%EC%84%B1-%EB%B0%A9%EB%B2%95)

***

### 기존 방식 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/7543c498-7e47-4b6d-a0a6-038f7f8bcb8a/image.png)

#### Live Streaming을 분리하면 생기는 문제 <a href="#live-streaming" id="live-streaming"></a>

![](https://velog.velcdn.com/images/van1164/post/7fe6ea86-44d5-441f-aa9f-52a376ac3d67/image.png)

**`User`**클래스를 **`Security`** , **`Video`**, **`Live Streaming`**에서 모두 사용하며 `Live Streaming`을 따로 분리하게 되면 User 클래스와 Security를 복사해서 가져가야 했다. 뿐만 아니라 `S3`나 `FFmpeg` 와 같이 공통적으로 사용해야하는 기능도 복사해야한다.

### 멀티모듈 사용 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/390f74f3-926c-49ed-9106-11c7015e94bf/image.png)

#### 각 기능에 대해 모듈화를 진행하고 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/41841c6a-daf6-471a-9aee-e63e9b61bfab/image.png)

#### 각 서비스에서 필요한 모듈만 추가하여 어플리케이션을 구성하였다. <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/c2b3577c-ab03-438a-9a50-ded1930bec45/image.png)

이렇게 하면 각 서비스에서 공통적으로 사용하는 기능이 동일한 코드라는 것이 분명해진다. 이 점은 별 것아니라고 생각될 수도 있지만, 어플리케이션이 많이 분산될 수록 같은 코드를 여러번 수정해야하는 일은 큰 시간을 소요시킨다.

예를들어 **`User`**도메인에 nickName이라는 변수하나를 추가한다고 가정할때, 기존 방식으로 여러 어플리케이션이 나누어져 있다면 모든 어플리케이션 코드에서 User에 nickName이라는 변수와 그에 따른 함수들을 추가 해주어야한다. 이는 시간도 오래걸릴 뿐아니라, 복사하는 과정에서 실수가 발생하면 어플리케이션이 오류가 날 수 있다.

***

### 적용하면서 생겼던 문제들 <a href="#undefined" id="undefined"></a>

#### 순환 의존 문제 <a href="#undefined" id="undefined"></a>

> `Circular dependency between the following tasks`

자기가 자기 모듈을 implementation하면 생기는 오류였다.

**오류가 난 코드**

```kts
subprojects {

	dependencies {
    	implementation(project(":util"))
    }
```

루트 프로젝트 gradle에 모든 모듈에서 사용할 것같은 **`util`** 을 subproject모두에 implementation하였다. 이렇게 하니 util이 util을 의존하는 문제가 생긴다.

그래서, 각각 모듈의 gradle에서 필요한 것만 의존하도록 바꾸어서 해결하였다.

#### 다른 모듈의 bean을 자동주입하지 못하는 문제 <a href="#bean" id="bean"></a>

다른 모듈에서 등록한 bean을 자동 주입하는 것이 안되는 문제가 발생하였다.\
![](https://velog.velcdn.com/images/van1164/post/6e86cbba-1ccf-4c12-9d97-d31218532a08/image.png)

외부 모듈에 등록된 bean은 자체적으로 Scan을 하지 않는다.\
@ComponentScan을 통해 참조한 모듈들을 Scan하도록 구현하였다.

```kotlin
@ComponentScan(basePackages = ["com.van1164.common","com.van1164.util","com.van1164.security"])
@EnableR2dbcRepositories(basePackageClasses = [VideoR2DBCRepository::class])
```

### 정리 <a href="#undefined" id="undefined"></a>

멀티모듈을 적용해서 도메인(기능) 별로 모듈을 만들어 독립적으로 배포가 가능하게끔 구현할 수 있었다. 그 중에서도 필요한 모듈들만 의존해서 사용할 수 있고, 공통적으로 사용하는 모듈을 한번의 수정만으로 보수하고 동일하게 다른 어플리케이션에서 사용할 수 있다는 장점이 있었다.\
물론, 적용하는 과정에서 많은 오류들도 있었고 구조도 변경되었다. 하지만 이러한 과정이 코드들의 의존성을 분리시키도록 하였다.
