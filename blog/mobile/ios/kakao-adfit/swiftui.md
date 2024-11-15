# SwiftUI 배너 광고

{% embed url="https://adfit.github.io/adfit-ios-sdk/documentation/adfitsdk/swiftui+bannerad" %}

```swift
import SwiftUI
import AdFitSDK


struct ContentView: View {
    var body: some View {
        AdFitBannerPresentableView(
            clientId: "광고단위를 입력해 주세요.",
            adUnitSize: "320x50"
        )
    }
}
```

