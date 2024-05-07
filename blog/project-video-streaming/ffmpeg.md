# FFmpeg 학습

![](https://velog.velcdn.com/images/van1164/post/5fbcf26a-3466-4777-ba7d-75d1b1bde160/image.jpg)

> 🛠 짧은 영상이어도 동영상을 업로드하고 보려면 **모든 파일을 받아와서 실행**해야한다.\
> 하지만 실제 유튜브나 타 스트리밍사이트는 **영상을 바로 불러오는데** 이 점이 궁금해서 시작하게 되었다.

#### Tiving에 네트워크 창을 열어보니 ts파일을 시간마다 불러오는 것을 알 수있었다 <a href="#tiving-ts" id="tiving-ts"></a>

![](https://velog.velcdn.com/images/van1164/post/5ec431f7-856b-460d-84ce-d62a5783f2d8/image.png)

> 🎁 알아보니 동영상파일을 잘게 자른 파일이 **.ts**파일이고 **FFmpeg**를 사용해서 **.ts**파일로 쪼개는게 가능하다.

### ts파일을 활용한 스트리밍을 위해 FFmpeg 사용 <a href="#ts-ffmpeg" id="ts-ffmpeg"></a>

> ⌛ 명령어를 알아내는 것이 쉽지 않았는데... 결국 **하나의 mp4**를 **여러 .ts파일로** 바꾸는 방법을 알아냈다.

**input.mp4를 h264코덱을 가진 ts파일로 변경**

```
ffmpeg -i input.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts output.ts
```

**input.mp4를 h264코덱을 가진 ts파일로 변경**

```
ffmpeg -i output.ts -c copy -map 0 -segment_time 3 -f segment -reset_timestamps 1 output_%03d.ts
```

* \-segmet\_time 3 은 3초간격으로 자른다는 의미

![](https://velog.velcdn.com/images/van1164/post/f85a3a3f-ec39-45ff-ba07-897a4ffb6de2/image.png)

***

### Spring에서 FFmpeg 사용하기 <a href="#spring-ffmpeg" id="spring-ffmpeg"></a>

#### FFmpeg Config 작성 <a href="#ffmpeg-config" id="ffmpeg-config"></a>

```kotlin
@Configuration
class FFmpegConfig(
    @Value("\${ffmpegPath}")
    val ffmepgPath : String,

    @Value("\${ffprobePath}")
    val ffprobePath : String
) {

    @Bean
    fun getFFmpeg() : FFmpeg{
        return FFmpeg(ffmepgPath)
    }

    @Bean
    fun getFFprobe() : FFprobe{
        return FFprobe(ffprobePath)
    }
}
```

#### shell을 코드로 작성 <a href="#shell" id="shell"></a>

```kotlin
//mp4 to ts
val builder = FFmpegBuilder()
    .setInput(inputFilePath.toString())
	.addOutput(tsFilePath).addExtraArgs("-c","copy")
	.addExtraArgs("-bsf:v","h264_mp4toannexb")
	.addExtraArgs("-f", "mpegts")
	.setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
	FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()
```

```kotlin
//ts 분할
val segmentBuilder =
    FFmpegBuilder().setInput(tsFilePath)
    .addOutput("${tsFilePath}_%03d.ts")
    .addExtraArgs("-c", "copy")
    .addExtraArgs("-map", "0")
    .addExtraArgs("-segment_time", "5")
    .addExtraArgs("-f", "segment")
    .addExtraArgs("-reset_timestamps", "1")
    .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
    .done()
    FFmpegExecutor(ffmpeg, ffprobe).createJob(segmentBuilder).run()
```

![](https://velog.velcdn.com/images/van1164/post/5e1d9c24-68a0-40af-b423-da52335af9b8/image.png)

### 다음 포스팅에서는 분리한 ts파일을 저장하고 스트리밍하는 코드를 작성해보겠습니다 😋 <a href="#ts" id="ts"></a>

#### 🩳참고 <a href="#undefined" id="undefined"></a>

[https://velog.io/@dogineer/%EC%9E%90%EB%B0%94-%EC%8A%A4%ED%94%84%EB%A7%81%EC%97%90%EC%84%9C-FFmpeg-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0](https://velog.io/@dogineer/%EC%9E%90%EB%B0%94-%EC%8A%A4%ED%94%84%EB%A7%81%EC%97%90%EC%84%9C-FFmpeg-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0)

[https://m.blog.naver.com/awspro/221965445828](https://m.blog.naver.com/awspro/221965445828)
