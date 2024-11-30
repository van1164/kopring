# drawable 이미지 띄우기



```kotlin
Image(
    painter = painterResource(id = R.drawable.your_drawable_resource), 
    contentDescription = "Description of the image",
    contentScale = ContentScale.Crop, // 이미지 크기 조정
    modifier = androidx.compose.ui.Modifier.size(100.dp) // 크기 조정
)
```

