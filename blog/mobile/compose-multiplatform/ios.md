# IOS 키보드 내리기

### CommonMain

```kotlin
expect fun hideKeyBoard() : Unit
```

### IosMain

```kotlin
import platform.UIKit.*

actual fun hideKeyBoard() {
    val keyWindow = UIApplication.sharedApplication.keyWindow
    keyWindow?.endEditing(true)
}
```

