# iOS 언어 설정 가져오기

**사용자에게 설정된 iOS 시스템 언어**를 얻어야 할 때, `NSLocale.preferredLanguages`를 직접 읽는 것이 가장 안전.&#x20;

원래는 `autoupdatingCurrentLocale.languageCode`  를 사용해 보았는데, 기본 지역 설정이 `en`인 경우에 언어가 한국어여도  영어가 반환되는 문제가 있었다.

***

### 1. commonMain

```kotlin
// commonMain
expect fun currentLanguageCode(): String
```

* 모든 플랫폼에서 동일한 API를 사용하기 위해 `expect` 함수로 선언합니다.

***

### 2. iOS 구현

```kotlin
// iosMain
import platform.Foundation.NSLocale

actual fun currentLanguageCode(): String {
    // ex) ["ko-KR", "en-US"] 형태의 목록
    val first = NSLocale.preferredLanguages.firstOrNull() as? String ?: "en"
    return first.substringBefore('-') // "ko-KR" → "ko"
}
```

#### 왜 `preferredLanguages`인가?

| 메서드                                               | 반환 값 예시              | 주의점                                          |
| ------------------------------------------------- | -------------------- | -------------------------------------------- |
| `NSLocale.autoupdatingCurrentLocale.languageCode` | `en`                 | 기본 지역이 영어(미국)면 영어가 우선될 수 있음                  |
| **`NSLocale.preferredLanguages`**                 | `["ko-KR", "en-US"]` | “설정 > 일반 > 언어 및 지역”에서 사용자가 직접 지정한 순서를 그대로 보장 |

***

### 3. Android 구현

```kotlin
// androidMain
import java.util.Locale

actual fun currentLanguageCode(): String = Locale.getDefault().language
```
