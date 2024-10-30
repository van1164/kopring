# 썸네일 만들기

> 😁 업로드한 동영상 목록을 만드려고보니 **썸네일**이 필요하다고 생각했다. 우선적으로 **FFmpeg**가 할 수있는지 찾아보았는데**!!** 역시나 **FFmpeg**는 가능했다. 🤣

#### FFmpeg를 사용해 동영상의 특정 부분 프레임을 자르기 <a href="#ffmpeg" id="ffmpeg"></a>

**1. ffmpeg 커멘드라인 실행**

ffmpeg 공식문서를 찾아보니 아래와 같은 코드로 썸네일을 생성할 수있었다.

```
ffmpeg -i input.mp4 -ss 00:00:10 -vframes 1 thumbnail.jpg
```

> **-ss** : 원하는 시간대\
> **-vframes** 1 : 원하는 프레임수

**2. kotlin 코드로 작성**

```kotlin
private fun extractThumbnail(inputFilePath: String, thumbNailPath: String) {
    FFmpegBuilder()
        .setInput(inputFilePath)
        .addOutput(thumbNailPath)
        .addExtraArgs("-ss", "00:00:1")
        .addExtraArgs("-vframes", "1")
        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
        .done()
        .apply {
            FFmpegExecutor(ffmpeg, ffprobe).createJob(this).run()
        }

}
```

#### 실행화면 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/b2a5777d-3591-4b41-bf42-5f5f99b5a588/image.png)

> 다음 목표는 webflux를 이용해 리액티브 프로그래밍을 적용시켜 보는 것이다\~\~ 😋
