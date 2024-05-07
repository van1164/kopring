# Chunk로 나눠서 업로드

![](https://velog.velcdn.com/images/van1164/post/e15c8e26-6a9c-464e-b665-9611a1867fe9/image.jpg)

> 💡 동영상을 업로드할 때 **용량이 크기때문에** 한번에 올리게 되면 **얼마나 업로드되었는지**, **얼마나 남았는지** 알 수없었다.\
> 서버에서도 한번의 요청에 한번에 응답만 가기때문에 얼마나 진행되었는지 알려줄 수 없기 때문이다.\
> 그래서 **Chunk개념**을 이용해보기로 했다.

### HTTP 206코드 <a href="#http-206" id="http-206"></a>

![](https://velog.velcdn.com/images/van1164/post/21cf212e-da57-4500-93b0-944f61497c04/image.png)

HTTP 상태코드에는 **206 Partial Content**라는 코드가 존재한다. 전체 Content가 다 온것은 아닌지만 **일정부분**만을 보냈고 그거에 대한 **응답이 처리되었을 때** 이 코드를 보낸다.\
**이 코드를 활용해서 구현할 생각이다.**

> 🥞 클라이언트에서 파일을 같은 용량 **여러chunk로** 나누기 => 하나씩 요청을 보냄 => 서버는 마지막인지 체크해서 **마지막이면 200**, **아니면 206**을 반환 => 클라이언트는 응답이 **206이면 다음 chunk**를 보냄

#### 요청 DTO <a href="#dto" id="dto"></a>

```
data class UploadVideoPartDTO(
    val title : String,
    val chunkNumber : Int,
    val totalChunk : Int,
)
```

#### Controller 구현 <a href="#controller" id="controller"></a>

```
    @PostMapping("/video")
    fun uploadVideo(@RequestPart(name = "video") video: MultipartFile,
                    @RequestPart(name = "videoData")videoData: UploadVideoPartDTO): ResponseEntity<Any> {

        return uploadService.uploadVideoPart(video, videoData)


    }
```

#### Service구현 <a href="#service" id="service"></a>

```
    fun uploadVideoPart(video: MultipartFile, videoData: UploadVideoPartDTO): ResponseEntity<Any> {

        uploadRepository.uploadVideoPart(video, videoData.chunkNumber) //S3에 업로드

        if (videoData.totalChunk - 1 == videoData.chunkNumber) {
        	//여러 part를 하나의 파일로 만들기
            val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
            Files.createFile(inputFilePath)
            
            for (i: Int in 0 until videoData.totalChunk) {
                val videoPart = uploadRepository.getPart(bucketUrl, video.originalFilename, i) ?: return ResponseEntity(
                    HttpStatus.BAD_REQUEST
                ) //S3에서 i번째 파트 가져오기
                Files.write(inputFilePath, videoPart.readAllBytes(), StandardOpenOption.APPEND)
                uploadRepository.deletePart(video.originalFilename, i)//i번째 파트 S3에서 제거
            }
            
                        //mp4 to ts
            mp4ToTs(inputFilePath, tsFilePath)

            //ts 분할
            divideTsFile(tsFilePath)

            // 여러 TS들을 S3에 업로드
            uploadRepository.uploadVideoTs(tsFilePath)
            
            return ResponseEntity(HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.PARTIAL_CONTENT)
        }
```

```
    private fun divideTsFile(tsFilePath: String) {
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
        File(tsFilePath).delete()
    }

    private fun mp4ToTs(inputFilePath: Path, tsFilePath: String) {
        val builder = FFmpegBuilder()
            .setInput(inputFilePath.toString())
            .addOutput(tsFilePath).addExtraArgs("-c", "copy")
            .addExtraArgs("-bsf:v", "h264_mp4toannexb")
            .addExtraArgs("-f", "mpegts")
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL).done()
        FFmpegExecutor(ffmpeg, ffprobe).createJob(builder).run()
        File(inputFilePath.toString()).delete()
    }
```

### 결과 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/be92d9bf-1bea-4390-88c3-13cfc34e925f/image.png)

> ❓️ 현재까지 한 생각은 **MP4파일을 여러 .ts로 변환**하기 위해서는 우선 MP4파일을 저장해야하는데, 이걸 로컬에 저장하면, **사용자가 매우 늘어났을 때 문제가 생길 것으로 판단**했다.\
> 그래서 S3에 **각 Chunk를 저장**하고 모든 Chunk를 다 받으면 **하나씩 불러와서 MP4로 저장**후 바로 ts로 **변경 및 분할후 삭제하는 방식**을 채택하였다. 최대한 로컬 머신에 저장되는시간을 줄이려는 판단이었는데.. 이보다 **더 좋은 방법을 찾아볼 예정**이다.
