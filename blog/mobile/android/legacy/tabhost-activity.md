# 기존 TabHost 방식에서 Activity탭 상태 변경하는 방법

### 발생한 문제&#x20;

오래된 코드라 Fragment가 아닌 Activity를 만들어서 탭화면을 전환하고 있었고, Activity들을 만들어놓고 호출하는 방식을 사용했다.

xzdsasdfs같은 탭 클릭시 탭 내부의 날짜를 오늘 날짜로 변경해야하는 요구사항이 있어서, Activity 외부에서 내부 함수를 호출하거나 Activity를 새로 시작할 필요가 있음.&#x20;

Activity를 새로 시작하는건 느려서 불필요함. 따라서 내부에서 오늘날짜로 변경하는 함수를 만들어 외부에서 실행하는 방향으로 구현.

```java
tabHost.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (tabHost.getCurrentTab() == 0) {
            Activity currentActivity = getLocalActivityManager().getCurrentActivity();
            if (currentActivity instanceof HomeActivity) {
                ((HomeActivity) currentActivity).resetToToday(); // 오늘 날짜로 변경
            } else {
                tabHost.setCurrentTab(0);
            }

        } else {
            tabHost.setCurrentTab(0);
        }
    }
});
```



**🚀 결론**

* ✅ **기존 `TabHost`에서 `getLocalActivityManager()`로 해결했지만, 이 방식은 오래된 구조.**
* ✅ **최신 방식에서는 `BottomNavigationView + Fragment`로 변경하는 것이 가장 일반적.**
* ✅ **더 나아가 최신 앱에서는 Jetpack Compose로 `Scaffold + NavHost`를 사용해서 관리.**











