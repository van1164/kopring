# Webflux 환경에서 MultiPart로 파일 업로드 (feat. S3 업로드하는 법까지)

## 😅 Spring 리액티브 환경에서는 멀티파트가 다르다? <a href="#spring" id="spring"></a>

### 기존 MultiPart 방식 <a href="#multipart" id="multipart"></a>

```kotlin
@PostMapping("")
@ResponseBody
fun testOne(@RequestPart(name = "video") video: MultipartFile, title : String): String {
    return "test"
}
```

Servlet방식의 `Spring`을 사용할 때는 전혀 이상없이 동작했던 코드입니다. 아마 대부분의 `Spring`개발자분들이 이런 방식을 통해서 이미지나 동영상을 업로드받을 것이다.

**심지어** 환경을 리액티브로 바꾸지 않고 **`Webflux(안의 Reactor)`**&#xB9CC; 사용할 때에는 이런 문제가 생기지 않았다. 하지만 모든 요청과 Security를 리액티브로 변경하기 위해 다음과 같이 `Properties`를 적용해주었다.

```
spring.main.web-application-type=reactive
```

![](https://velog.velcdn.com/images/van1164/post/44d2dd23-da3d-4556-a4b3-1d1dce294205/image.png)

그 후부터 문제가 발생했다. 기존의 REST API가 대부분 오류가 난다.. 특히 `415`나 `500` 코드의 오류들이었다. 그래서 왜 문제가 생기는지 10시간 정도 찾아보았습니다. `Security`를 리액티브로 구현하는 작업도 같이 하고 있었기 때문에 처음에 `Security`의 문제라고 생각한 것이 더 오래 걸리게 만들었다.

열심히 하나하나 뜯어서 실험해보다 보니 MultiPartFile이 전달되지 않는 것을 확인할 수 있었다. 그래서 왜 받아오지 못하는지 알아보았지만, `WebFlux`에 대한 정보가 정말 없었습니다... **그러다 찾아낸 해결책.**

## Webflux환경에서는 MultiPartFile을 사용하지 않는다! <a href="#webflux-multipartfile" id="webflux-multipartfile"></a>

왜 무엇때문에 사용하지 않는지는 더 찾아봐야 겠지만, 확실한건 Request에서 MulitPartFile을 인식하지 못한다. 대신에 `FilePart`라는 `Content`를 `Flux`로 감싸고 있는 객체를 사용한다!!\
이 점에서 봤을 때 `MultiPartFile` 대신 `FilePart`를 사용하는건 아무래도 파일을 Flux와 같은 스트림 형태로 전달하기 위함이 아닐까 생각한다.

### FilePart사용법 <a href="#filepart" id="filepart"></a>

```kotlin
@PostMapping("")
@ResponseBody
fun test(@RequestPart(name = "video") video: FilePart,
         @RequestPart title : String,
    ): Mono<String> {
    return Mono.just("test")
   }
```

이렇게 `MultiPartFile`을 `FilePart`로만 바꾸어주면 됩니다. 이렇게 받아온 `FilePart`를 어떻게 활용하면 될까요?

### FilePart를 File로 바꾸기 <a href="#filepart-file" id="filepart-file"></a>

```kotlin
val path = Paths.get("지정할 URI/test.mp4") //지정한 URI에 test.mp4로 저장됩니다.
video.transferTo(path).subscribe()
```

### FilePart를 InputStream으로 바꾸기 <a href="#filepart-inputstream" id="filepart-inputstream"></a>

이 방법을 찾아내기가 쉽지 않았다. 우선 처음에 생각했던 틀린방법부터 보여드리겠습니다.

#### 🤬 틀린방법 <a href="#undefined" id="undefined"></a>

```kotlin
video.content()
    .map(DataBuffer::asInputStream)
    .doOnNext { inputStream->
        inputStream
    }
```

직관적으로 봤을 때는 전혀 이상하다고 생각하지 않았는데 이렇게 동작시켜보니 1kb짜리 inputStream으로 쪼개져서 나왔다. 아무래도 거의 모든 경우 업로드한 파일은 그 자체로 의미가 있기 때문에 파일 자체의 inputStream이 필요했다.

#### 😋 파일 자체를 InputStream으로 바꾸는 법 <a href="#inputstream" id="inputstream"></a>

```kotlin
DataBufferUtils.join(video.content())
			.map(DataBuffer::asInputStream)
// DataBufferUtils를 사용해서 Flux<DataBuffer> 를 하나의 DataBuffer로 합쳐준다.
```

#### S3에 업로드하는 방법 <a href="#s3" id="s3"></a>

```kotlin
DataBufferUtils.join(video.content())
            .map(DataBuffer::asInputStream)
            .doOnNext { inputStream ->
                amazonS3.putObject(
                PutObjectRequest("버킷이름", "test.mp4", inputStream, ObjectMetadata())
                )
            }
```

다음과 같이 업로드하면 된다.
