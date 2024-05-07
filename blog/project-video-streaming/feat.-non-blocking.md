# 업로드 시간 이게 최선일까? (feat.비동기 Non-Blocking)

![](https://velog.velcdn.com/images/van1164/post/2b2fcf5e-db85-4d7e-ae6e-7f661fea7e1f/image.gif)

> 지난번에는 스트리밍할 동영상업로드를 구현하면서 Chunk별 분할 업로드를 구현했었다.\
> 여기서 생긴 성능에 대한 궁금증들을 실험해보는 과정들을 적어보겠다.\
> 소스코드 [https://github.com/van1164/video-streaming](https://github.com/van1164/video-streaming)

### ❓ 업로드 시간은 이게 최선일까? <a href="#undefined" id="undefined"></a>

#### 프론트에서의 Log <a href="#log" id="log"></a>

![](https://velog.velcdn.com/images/van1164/post/87d1917e-e985-449e-982f-d15e25dd56bd/image.png)

> html코드에서 로그를 찍어보니 모든 Chunk를 업로드하는데 걸리는 시간이 약 **2.5초 정도** 걸리고 **최종적으로 걸리는 시간은 10초정도**로 나왔다. 그렇다면 s3에 접근해서 모든 Chunk를 mp4로 변환해서 hls파일로 변경후 다시 **S3에 업로드하는 과정에서 대략 8초정도** 걸린 것을 알 수 있다.

**❗ 그렇다면 서버에서의 작업들은 각각 얼마나 걸렸을까?**

\


#### 서버에서의 Log <a href="#log" id="log"></a>

**1. Chunk들을 MP4로 합치는 과정 (약 2.3초)**

![](https://velog.velcdn.com/images/van1164/post/b058ca76-f77c-4c66-8e8f-d5287c86c271/image.png)

**2. 썸네일을 만들고 업로드하는데 걸린 시간 (0.25초)**

![](https://velog.velcdn.com/images/van1164/post/f94377ef-2334-4457-b8d8-ea9933eadd95/image.png)

**3. mp4를 hls로 바꾸고 업로드하는 데 걸린 시간 (4.5초)**

![](https://velog.velcdn.com/images/van1164/post/10f6f4bb-2255-4525-b08b-1d167a6517c2/image.png)

#### ❗❗ 여기서 2가지의 궁금증이 생겼다. <a href="#id-2" id="id-2"></a>

* **여러 Chunk들을 동시에 업로드해서 시간을 줄일 수는 없을까?**
* **비동기적으로 실행가능한 건 동시에 실행하면 시간을 줄일 수 있지 않을까?**

***

### 1. 여러 Chunk들을 동시에 업로드해보기 <a href="#id-1-chunk" id="id-1-chunk"></a>

> 기존에는 Chunk들을 순차적으로 보내는 방식을 사용했었다. 이번에는 모든 Chunk에 대한 요청을 동시에 보내고 Promise를 통해 다 보내지면 그 다음 동작을 하도록 나누어 보았다.\
> 그에 맞게 서버 API도 새로운 구성을 추가했다.

![](https://velog.velcdn.com/images/van1164/post/9694046e-4c06-479e-91c7-bfeeb7b9f19b/image.png)

### 약 270MB 영상으로 업로드 속도 비교 <a href="#id-270mb" id="id-270mb"></a>

#### 기존방식 (약 115초) <a href="#id-115" id="id-115"></a>

![](https://velog.velcdn.com/images/van1164/post/28c87411-a0f8-4090-b195-a04da5893dcd/image.png)

#### Promise를 사용한 방식 (약 96초) <a href="#promise-96" id="promise-96"></a>

![](https://velog.velcdn.com/images/van1164/post/556418eb-b06e-475a-a889-ad8cb1e2f03b/image.png)

#### ✅ 동시에 여러사람이 업로드했을 경우도 비교를 해보아야겠지만, 우선적으로 한명에 대해서는 비동기적으로 처리한게 더 빨랐다. <a href="#undefined" id="undefined"></a>

\


***

\


### 2. 비동기적으로 할 수있는건 비동기적으로 하기 (feat. Completable Future) <a href="#id-2-feat-completable-future" id="id-2-feat-completable-future"></a>

#### 기존 방식 (약 96초) <a href="#id-96" id="id-96"></a>

![](https://velog.velcdn.com/images/van1164/post/af9d6a6b-602e-4965-968c-497d1c370c6d/image.png)

#### 비동기적인 방식 (약 81초) <a href="#id-81" id="id-81"></a>

![](https://velog.velcdn.com/images/van1164/post/b4de1682-d47a-454b-a9d2-f84ac7447741/image.png)

![](https://velog.velcdn.com/images/van1164/post/f9ca7507-aba9-436d-bb50-a0cda9332657/image.png)

**코드**

```
    fun uploadVideoPartLast(video: MultipartFile, videoData: UploadVideoPartDTO): String {

        val futureList = mutableListOf<CompletableFuture<ByteArray>>()
        //여러 part를 하나의 파일로 만들기
        val stopWatch = StopWatch()
        stopWatch.start("mp4로 만드는데 걸린 시간")
        //val mp4start = System.currentTimeMillis()
        val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
        runBlocking {
            Files.createFile(inputFilePath)
        }


        for (i: Int in 0 until videoData.totalChunk) {
            futureList.add(CompletableFuture.supplyAsync {
                return@supplyAsync uploadRepository.getPartByteArray(
                    bucketUrl,
                    video.originalFilename,
                    i
                )
            })
            //val videoPart = uploadRepository.getPart(bucketUrl, video.originalFilename, i)
        }


        return CompletableFuture.allOf(*futureList.toTypedArray())
            .thenApply {
                // ts -> mp4
                futureList.forEach{videoPart ->
                    Files.write(inputFilePath, videoPart.get(), StandardOpenOption.APPEND)
                }
                stopWatch.stop()
            }.thenApplyAsync {
                val outputUUID = UUID.randomUUID().toString()
                val m3u8Path = "$outputUUID.m3u8"
                val thumbNailPath = UUID.randomUUID().toString() + ".jpg"
                val deleteChunkFuture = CompletableFuture.runAsync{deleteChunkFiles(videoData, video)}
                val thumbNailFuture = CompletableFuture.runAsync{createThumbNail(inputFilePath, thumbNailPath)}
                val saveDataFuture = CompletableFuture.runAsync{saveVideoData(outputUUID, videoData, thumbNailPath)}
                val mp4ToHlsFuture = CompletableFuture.runAsync{mp4ToHls(inputFilePath, m3u8Path, outputUUID)}
                CompletableFuture.allOf(deleteChunkFuture,thumbNailFuture,saveDataFuture,mp4ToHlsFuture).get()
                return@thenApplyAsync outputUUID
            }.get()
    }

    private fun mp4ToHls(
        inputFilePath: Path,
        m3u8Path: String,
        outputUUID: String
    ) {
        logger.info("hls시작")
       val stopWatch = StopWatch()
        stopWatch.start("mp4를 hls로 바꾸고 업로드하는 데 걸린 시간")
        //mp4 to ts


        mp4ToM3U8(inputFilePath, m3u8Path, outputUUID)


        // 여러 TS들을 S3에 업로드
        uploadVideoTs(outputUUID)

        uploadRepository.uploadM3U8(m3u8Path)
        stopWatch.stop()
        println(stopWatch.prettyPrint())
    }

    private fun createThumbNail(
        inputFilePath: Path,
        thumbNailPath: String
    ) {
        logger.info("썸네일")
        val stopWatch = StopWatch()
        stopWatch.start("썸네일 만들고 업로드하는 데 걸린 시간")
        //thumbnail by ffmpeg

        extractThumbnail(inputFilePath.toString(), thumbNailPath)
        //uploadThumbnail
        uploadRepository.uploadThumbnail(thumbNailPath)
        stopWatch.stop()
        println(stopWatch.prettyPrint())
    }

    private fun deleteChunkFiles(
        videoData: UploadVideoPartDTO,
        video: MultipartFile
    ) {
        logger.info("DELETE")
        val futures = (0 until videoData.totalChunk).map {
            CompletableFuture.runAsync { uploadRepository.deletePart(video.originalFilename, it) }
        }
        CompletableFuture.allOf(*futures.toTypedArray()).get()
    }
```

#### ✅ 같은 용량의 파일을 업로드하는 데 13초정도의 시간 절약을 할 수있었다!! <a href="#id-13" id="id-13"></a>

***

### 😊 참고 <a href="#undefined" id="undefined"></a>

#### Spring StopWatch <a href="#spring-stopwatch" id="spring-stopwatch"></a>

**시간을 출력해보면서 새로 알게되었는데, 시간측정할 때 코드를 좀 예쁘게 작성할 수있고 출력도 예쁘게 가능했다.**

예시 코드)

```
val stopWatch =StopWatch()
stopWatch.start("mp4로 만드는데 걸린 시간")
//코드
stopWatch.stop()
```

![](https://velog.velcdn.com/images/van1164/post/eed4d126-c7da-459f-8030-cdb26994a443/image.png)

***

#### Error : Timeout waiting for connection from pool <a href="#error--timeout-waiting-for-connection-from-pool" id="error--timeout-waiting-for-connection-from-pool"></a>

> **S3 Object를 여러 개 동시에 읽어올 때 생길 수있는 문제**

amazonS3 `S3Object`를 close해주지 않았기 때문에 다음과 같은 오류가 발생하였다. `S3Object`는 `Closeable`을 **implements**하고 있기 때문에 **`try-with-resources`**를 사용할 수있다.

**`try-with-resources`**란 `AutoCloseable` 인터페이스를 구현하고 있는 자원에 대해 try안에 그 자원을 넣으면 작업이 끝나면 **자동으로 close**해주는 것을 말한다.

![](https://velog.velcdn.com/images/van1164/post/c37bf012-f634-4442-bd6f-bf74c4d24241/image.png)

**코틀린에서 사용법!**

코틀린에서는 use 고차함수를 통해 사용할 수 있다.

```
fun readFirstLine(path: String): String {
    BufferedReader(FileReader(path)).use { br ->
        return br.readLine()
    }
}
```

[https://blog.naver.com/dnjung/221168057295](https://blog.naver.com/dnjung/221168057295)\
[https://aws.amazon.com/ko/blogs/developer/closeable-s3objects/](https://aws.amazon.com/ko/blogs/developer/closeable-s3objects/)\
[https://shinjekim.github.io/kotlin/2019/11/01/Kotlin-%EC%9E%90%EB%B0%94%EC%9D%98-try-with-resource-%EA%B5%AC%EB%AC%B8%EA%B3%BC-%EC%BD%94%ED%8B%80%EB%A6%B0%EC%9D%98-use-%ED%95%A8%EC%88%98/](https://shinjekim.github.io/kotlin/2019/11/01/Kotlin-%EC%9E%90%EB%B0%94%EC%9D%98-try-with-resource-%EA%B5%AC%EB%AC%B8%EA%B3%BC-%EC%BD%94%ED%8B%80%EB%A6%B0%EC%9D%98-use-%ED%95%A8%EC%88%98/)
