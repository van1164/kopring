# Live Activity에서 딥링크로 바로 이동시키기

### 딥링크를 걸어야 하는 이유

Live Activity는 잠금 화면·동적 섬에 실시간 상태를 보여준다.\
사용자가 더 자세한 정보를 보고 싶으면 한 번의 탭으로 상세 화면으로 가야 했다.\


### 라이브액티비티 딥링크 연결 방법

```swift
LiveMatchView(context: context)
    .widgetURL(URL(string: "K-League://match/\(context.attributes.gameKey)"))

```

이렇게 widgetURL을 사용하면 라이브 액티비티 클릭시 `K-League://match/{gameKey}`를 열도록 설정할 수있다.\


### 딥링크 받아서 세부 화면으로 연결하기

```objectivec
- (BOOL)application:(UIApplication *)app
            openURL:(NSURL *)url
            options:(NSDictionary<UIApplicationOpenURLOptionsKey,id> *)options {

    NSLog(@"URL opened: %@", url.absoluteString);

    if ([url.scheme isEqualToString:@"K-League"] &&
        [url.host   isEqualToString:@"match"]) {

        NSString *gameKey = url.pathComponents.lastObject;
        NSLog(@"GameKey: %@", gameKey);

        // 1. API URL을 만든다.
        NSString *api = [NSString stringWithFormat:
            @"game.yam?gameKey=%@",
            [gameKey stringByAddingPercentEncodingWithAllowedCharacters:
                NSCharacterSet.URLFragmentAllowedCharacterSet]];
        NSURL *apiUrl = [NSURL URLWithString:API(api)];
        NSLog(@"API URL: %@", apiUrl.absoluteString);

        // 2. API를 호출한다.
        [NetworkHelper request:apiUrl
                      delegate:self
                         name:@"GAME_DETAIL_FROM_URL"];

        return YES;
    }
    return NO;
}
```

우리팀은 Objective C로 앱이 개발되어 있어서 위와 같이 처리하였다.

***

