# iOS 웹뷰에서 fixed한 광고가 사라지는 문제



### 1. 현상

* 하단 광고 배너를 `position: fixed`로 붙여 놨음
* 부모 컨테이너는 `overflow‑y: scroll`(모멘텀 스크롤)로 콘텐츠를 스크롤했음
* **iOS WebView**에서만 스크롤 중 배너가 깜빡이거나 통째로 사라졌음

***

2\. 원인

* iOS 모멘텀 스크롤은 스크롤 중 **레이아웃 계산**을 최소화하려고 부모만 이동시킴
* 부모도 `fixed`, 자식도 `fixed`이면 스크롤 중 **자식의 위치를 갱신하지 않음**
* 뷰포트 기준으로 자식이 바깥으로 계산돼서 안 보이는 상태가 됨

***

### 3. 삽질 로그

1. `position: sticky`, `transform: translateZ(0)` 전부 테스트 → 다 실패
2. 자식 `fixed`를 `absolute`로 바꿨더니 페이지 끝에 붙어서 의미 없었음
3. 결론: **스크롤 영역과 배너를 형제 노드**로 분리

***

### 4. 최종 구조

```
.wrapper
 ├─ .scrollArea      ← overflow-y: scroll; height: calc(100vh - 배너높이)
 └─ .banner(fixed)   ← fixed bottom: 0; width: 100%
```

```tsx
return (
  <div className="w-full h-full bg-white text-black overscroll-y-none">
    {/*
      1) 스크롤 가능한 컨테이너
         - 여기에서 iOS 모멘텀 스크롤을 사용
         - height 계산 시 배너 높이만큼 빼주거나, 아래쪽에 padding을 넣어
           배너가 콘텐츠와 겹치지 않도록 처리할 수 있음
    */}
    <div
      className="overflow-y-scroll"
      style={{
        height: 'calc(100vh - 100px)',
        WebkitOverflowScrolling: 'touch', // iOS 모멘텀 스크롤
      }}
    >
      <BackToListLink app={app} os={os} />
      <NoticeDetailContent post={post} />
      <BackToListLink app={app} os={os} />
    </div>

    {/*
         - 스크롤 영역과는 형제 노드
         - iOS에서 부모도 fixed, 자식도 fixed가 아니므로 깜빡임 현상 없음
    */}
    <div
      className="fixed left-0 bottom-0 w-full bg-white z-10"
      style={{ height: '100px' }} // 배너 높이
    >
      <AdfitBanner />
    </div>
  </div>
);
```

