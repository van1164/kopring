# HLS(Http Live Streaming) 적용기

![](https://velog.velcdn.com/images/van1164/post/7c9b65b1-521e-4630-aa43-d17f8ab09eae/image.gif)

> **.mp4**를 **.ts**파일로 분할을 했지만\
> 이걸 어떻게 연결해서 재생하고, 원하는 시간에 맞는 **.ts**파일을 불러올 수있을까 고민하였다.
>
> **하지만!!! 열심히 조사해보니 m3u8이라는 파일을 알아냈다 ㅋㅋㅋㅋ**
>
> **이에 대한 설명과 적용해서 동영상 재생까지 아래에서 설명한다.**

### .m3u8 파일이란?? <a href="#m3u8" id="m3u8"></a>

> .m3u8 파일 형식은 오디오 및 비디오 재생 프로그램에서 재생 목록을 저장하는 데 사용한다.

#### 생성된 .m3u8 예시 <a href="#m3u8" id="m3u8"></a>

![](https://velog.velcdn.com/images/van1164/post/a63f2b1f-ee83-4b9b-ac6a-2adb06220e35/image.png)

> #### FFmepg를 사용하면 m3u8과 함께 원하는 만큼의 .ts파일로 분할할 수 있다. <a href="#ffmepg-m3u8-ts" id="ffmepg-m3u8-ts"></a>

### .m3u8파일 생성하도록 Repository 변경 <a href="#m3u8-repository" id="m3u8-repository"></a>

```kotlin
private fun mp4ToM3U8(inputFilePath: Path, m3u8Path: String, tsFilePath : String) {
    val builder = FFmpegBuilder()
        .setInput(inputFilePath.toString())
        .addOutput(m3u8Path)
        .addExtraArgs("-c", "copy")
        .addExtraArgs("-bsf:v", "h264_mp4toannexb")
        .addExtraArgs("-hls_segment_filename","${tsFilePath}_%03d.ts")
        .addExtraArgs("-start_number","0")
        .addExtraArgs("-hls_time","5")
        .addExtraArgs("-hls_list_size","0")
        .addExtraArgs("-hls_base_url","https://video-stream-spring.s3.ap-northeast-2.amazonaws.com/")
        .addExtraArgs("-f", "hls")
        .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
    FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()
    File(inputFilePath.toString()).delete()
}
```

> #### 🌭 여기서 HLS란 <a href="#hls" id="hls"></a>
>
> **HTTP LIVE STREAMIN**G의 약자로 인터넷 속도에 맞게 비디오 재생을 최적화 하여 네트워크 상태에 동적으로 적응하는 **비디오 스트리밍 프로토콜**이다.\
> **즉,** 네트워크와 사용자의 요청에 의해 **필요한 만큼의 비디오**를 제공하는 프로토콜이다.

#### 업로드 화면 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/09b04127-c4a5-490c-80de-278623246bf2/image.png)

#### S3에 저장된 .ts와 .m3u8 파일 확인 <a href="#s3-ts-m3u8" id="s3-ts-m3u8"></a>

![](https://velog.velcdn.com/images/van1164/post/502b8a3d-67b0-4594-8f67-a59a7a1c3b18/image.png)

#### 실행화면 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/f4c30c6c-0cef-4349-861a-1c44dda512d8/image.gif)

**🥰테스트는 스토브리그 클립으로 진행했습니다 ㅎ**

**😊 링크도 바로 들어가지고 동영상도 바로바로 잘 나오네요!!!**

***

### 😙회고 <a href="#undefined" id="undefined"></a>

> 처음에는 **.ts파일들** 여러개를 가지고 시간을 계산해서 적절한 시간대에 적절한 **.ts파일**을 넘겨주어서 재생해야한다고 생각했다. ~~(물론 실제 서비스기업들은 자신만의 방법을 구현했을지도 모른다.)~~ 하지만, **.m3u8**를 사용하는 **HLS프로토콜**을 통해 재생목록기반으로 재생할 수있다는 것을 알게 되었고 이를 사용해서 동영상 스트리밍을 구현해볼 수있었다.\
> 다음에는 동영상을 저장할 때 **메모리나 저장소를 고려해서 업그레이드해볼 예정**이다.
