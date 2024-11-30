# Drawable 같이 사용하기

project View로 보기 한다음 commonMain -> composeResources에 xml을 넣으면 빌드시 자동생성된다.

<figure><img src="../../.gitbook/assets/image.png" alt=""><figcaption></figcaption></figure>

```kotlin
Image(
    painter = painterResource(Res.drawable.logo),
    contentDescription = "Description of the image",
    contentScale = ContentScale.Crop,
    modifier = androidx.compose.ui.Modifier.size(100.dp)
)
```

이렇게 Res를 통해 접근이 가능하다.
