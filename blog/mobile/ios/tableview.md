# TableView 기본 상단 패딩 제거

### **문제 상황**

iOS 15부터 `UITableView`에서 **섹션 헤더 위쪽에 불필요한 공백이 생기는 문제**가 발생했다.\
코드를 아무리 봐도 `tableView:viewForHeaderInSection:`에서 따로 여백을 준 적이 없는데, 섹션 헤더가 아래로 내려가고 위쪽에 빈 공간이 생긴다.

***

### **원인**

iOS 15부터 `UITableView`의 **섹션 헤더 위쪽에 기본 패딩**이 추가되었다.\
이 패딩은 `sectionHeaderTopPadding`이라는 프로퍼티로 관리되며, 기본값이 **22pt 정도**로 잡혀 있다.\
즉, 특별한 설정을 하지 않으면 **섹션 헤더가 자동으로 아래로 밀리면서 상단에 공백이 생긴다.**

***

### **해결 방법**

이 문제를 해결하려면 `sectionHeaderTopPadding`을 `0.0`으로 설정해 주면 된다.\
이 코드를 `viewDidLoad`에서 실행하면 적용된다.

#### **✅ 해결 코드**

```objc
- (void)viewDidLoad {
    [super viewDidLoad];

    if (@available(iOS 15.0, *)) {
        self.tableView.sectionHeaderTopPadding = 0.0;
    }
}
```

이렇게 하면 iOS 15 이상에서도 기존처럼 섹션 헤더가 **위쪽에 붙어서** 정상적으로 표시된다.

***

### **정리**

| iOS 버전    | `sectionHeaderTopPadding` 기본값 | 해결 방법                                              |
| --------- | ----------------------------- | -------------------------------------------------- |
| iOS 14 이하 | 없음 (영향 X)                     | X                                                  |
| iOS 15 이상 | 기본값 22pt (공백 발생)              | `self.tableView.sectionHeaderTopPadding = 0.0;` 적용 |

iOS 15 이후 UITableView에서 섹션 헤더 위에 **의도치 않은 공백이 생긴다면**,\
이 코드 한 줄이면 바로 해결된다! 🚀
