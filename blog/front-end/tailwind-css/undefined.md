# 다크 모드에서 트윗 임베드 '흰색 모서리' 현상

<div align="left"><figure><img src="../../.gitbook/assets/image.png" alt="" width="360"><figcaption></figcaption></figure></div>

## 1. 증상 정리

* `blockquote.twitter-tweet` → `widgets.js` 변환 후, **모서리(iframe 캔버스)가 흰색**으로 비쳐서 배경과 어긋남
* 라이트 모드에서는 정상
* `theme: 'dark'`, `data-border-color` 등 기존 옵션을 모두 넣어도 해결되지 않음



## 2. 원인 추적 과정

1. **Tailwind의 자동 규칙**\
   Tailwind v3는 다크 모드 클래스를 활성화하면 `html.dark { color-scheme: dark; }` 를 전역에 주입한다. \
   이는 브라우저에 “이 문서는 어두운 색 구성표를 쓴다”는 힌트를 준다.
2. **브라우저의 iframe 처리 방식**\
   부모 문서가 `color-scheme: dark`일 때, 투명도가 지정되지 않은 외부 `<iframe>` 캔버스는 사양·브라우저 구현에 따라 불투명하게 칠해질 수 있다. 크로미움 이슈 트래커에서도 “dark 모드가 iframe 투명도를 깨뜨린다”는 버그 리포트가 올라와 있다.
3. **Twitter 위젯 특성**\
   트윗 iframe 내부 CSS는 자체적으로 배경색을 칠할 것을 가정하고 있어 별도의 `color-scheme` 값을 명시하지 않는다. 따라서 부모의 `dark` 스킴이 그대로 전파돼 브라우저가 iframe 캔버스를 밝은 색으로 초기화해 버린다. 이에 따라 실제 트윗 카드 내부는 다크 테마로 그려지지만, 가장 바깥 둥근 영역이 흰색으로 비치게 된다. 같은 증상이 MkDocs-Material, Cal.com 등 오픈소스 프로젝트에서도 확인됐다.

**정리하면:** `color-scheme: dark`가 iframe 캔버스의 투명도를 깨고, 그 결과 둥근 모서리에 흰색 잔상이 생긴다.



## 3. 해결방법

```css
/* globals.css */
.twitter-tweet iframe {
  color-scheme: light; /* 투명도 유지 */
}
```

* `light`(또는 `normal`)로 돌려놓으면 브라우저는 “이 iframe은 밝은 스킴”으로 판단해 초기 배경을 **투명**으로 유지한다.
* Twitter가 iframe 내부에서 직접 다크 테마 배경을 칠하므로, Twitter는 다크모드로 보인다.

## 4. 참고

> **mkdocs-material**: “Dark mode embeds show white tweet corners” 이슈 #6889\
> [https://github.com/squidfunk/mkdocs-material/issues/6889?utm\_source=chatgpt.com](https://github.com/squidfunk/mkdocs-material/issues/6889?utm_source=chatgpt.com)

