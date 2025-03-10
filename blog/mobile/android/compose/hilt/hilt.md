# Hilt란?

안드로이드에서 Hilt를 사용하면 의존성 주입을 통해 UI 컴포넌트의 재사용성과 테스트 용이성을 높일 수 있다.&#x20;

### **1. Hilt 설정**

프로젝트에서 Hilt를 사용하려면 `build.gradle` 파일에 필요한 의존성을 추가.

```kts
kotlin코드 복사// build.gradle (프로젝트 수준)
buildscript {
    dependencies {
        classpath "com.google.dagger:hilt-android-gradle-plugin:버전번호"
    }
}

// build.gradle (앱 모듈 수준)
plugins {
    id 'com.android.application'
    id 'kotlin-android'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}

android {
    ...
    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion '버전번호'
    }
    ...
}

dependencies {
    implementation "com.google.dagger:hilt-android:버전번호"
    kapt "com.google.dagger:hilt-compiler:버전번호"
    // Jetpack Compose 의존성 추가
    implementation "androidx.compose.ui:ui:버전번호"
    implementation "androidx.compose.material:material:버전번호"
    implementation "androidx.compose.ui:ui-tooling-preview:버전번호"
    // 기타 필요한 Compose 의존성
}
```

### **2. Application 클래스 설정**

Hilt를 사용하려면 `@HiltAndroidApp` 어노테이션을 적용한 `Application` 클래스를 생성.

```kotlin
@HiltAndroidApp
class MyApplication : Application()
```

### **3. Activity에서 Hilt와 Compose 통합**

Hilt를 적용한 Activity에서는 `@AndroidEntryPoint` 어노테이션을 사용.

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}
```

### **4. ViewModel 주입**

Hilt를 통해 ViewModel을 주입하고 Compose에서 활용.

```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    // ViewModel 로직
}
```

Compose 함수 내에서 `hiltViewModel()` 함수를 사용하여 ViewModel을 가져옴.

```kotlin
@Composable
fun MyScreen(viewModel: MyViewModel = hiltViewModel()) {
    // UI 구성
}
```

### **5. Hilt 모듈 정의**

의존성 주입을 위한 모듈을 정의할 때 `@Module`과 `@InstallIn` 어노테이션을 사용.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideRepository(
        apiService: ApiService
    ): MyRepository {
        return MyRepositoryImpl(apiService)
    }

    // 기타 의존성 제공 메서드
}
```

### **6. Compose Preview에서 Hilt 사용**

Compose의 `@Preview` 기능을 사용할 때 Hilt를 적용하려면 `@HiltViewModel`과 `@Composable`을 조합하여 미리보기를 구성.

```kotlin
@Preview(showBackground = true)
@Composable
fun PreviewMyScreen() {
    MyApp {
        MyScreen()
    }
}
```

