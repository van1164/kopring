---
icon: k
---

# Koin

<figure><img src="../../.gitbook/assets/image (4).png" alt=""><figcaption></figcaption></figure>

{% embed url="https://insert-koin.io/docs/reference/koin-compose/compose/" %}

## Koin을 활용한 Compose Multiplatform에서의 의존성 주입

Compose Multiplatform 환경에서 의존성 주입을 구현할 때, 일반적으로 Android 개발에서 사용하는 Hilt는 멀티플랫폼을 지원하지 않아 사용이 제한된다. 이러한 한계를 극복하기 위해 Koin을 활용하면 멀티플랫폼 프로젝트에서도 효과적인 의존성 주입이 가능하다.

### Koin이란 무엇인가?

Koin은 Kotlin 언어로 작성된 경량의 의존성 주입 프레임워크로, 코드 작성이 간편하고 설정이 쉬워 Android 및 Kotlin 멀티플랫폼 프로젝트에서 널리 사용되고 있다. Koin은 런타임에 의존성을 해결하며, 애노테이션 프로세싱이나 코드 생성을 필요로 하지 않아 빌드 시간이 단축되는 장점이 있다.

***

### Compose Multiplatform에서 Koin 설정하기

1.  **의존성 추가**: 프로젝트의 `build.gradle.kts` 파일에 Koin 관련 의존성을 추가.

    ```kotlin
    dependencies {
        // Koin Core 라이브러리
        implementation("io.insert-koin:koin-core:3.5.0")

        // Koin Compose Multiplatform 라이브러리
        implementation("io.insert-koin:koin-compose:3.5.0")
    }
    ```
2.  **Koin 모듈 정의**: 의존성을 제공할 모듈을 정의.

    ```kotlin
    val appModule = module {
        single { MyService() }
        // 또는 constructor DSL을 사용할 수 있습니다.
        singleOf(::MyService)
    }
    ```
3.  **KoinApplication으로 Koin 시작하기**: Compose의 `@Composable` 함수 내에서 `KoinApplication` 함수를 사용하여 Koin을 시작.

    ```kotlin
    @Composable
    fun App() {
        KoinApplication(application = {
            modules(appModule)
        }) {
            // 화면 구성 요소 호출
            MyScreen()
        }
    }
    ```

    `KoinApplication` 함수는 Compose의 생명주기에 맞게 Koin 컨텍스트를 시작하고 종료하며, Compose 환경에서 Koin을 효과적으로 사용할 수 있음.
4.  **의존성 주입**: `@Composable` 함수 내에서 `koinInject()` 함수를 사용하여 필요한 의존성을 주입받을 수 있다.

    ```kotlin
    @Composable
    fun MyScreen() {
        val myService: MyService = koinInject()
        // myService를 활용한 UI 구성
    }
    ```

이러한 설정을 통해 Compose Multiplatform 환경에서도 Koin을 활용하여 간편하게 의존성 주입을 구현할 수 있다.

### Compose Preview에서 Koin 사용하기

Compose의 프리뷰 기능을 사용할 때도 `KoinApplication` 함수를 활용하여 Koin 컨텍스트를 설정할 수 있다.

```kotlin
@Composable
@Preview
fun PreviewApp() {
    KoinApplication(application = {
        // 프리뷰를 위한 모듈 설정
        modules(previewModule)
    }) {
        // 프리뷰를 위한 Compose UI 구성
    }
}
```

