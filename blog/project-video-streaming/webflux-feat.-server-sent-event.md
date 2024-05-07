# WebFlux적용기 (feat. Server Sent Event)

### Webflux 적용 <a href="#webflux" id="webflux"></a>

> 기존에 CompletableFuture로 구현된 파일 업로드를 WebFLux를 적용해 보았다.\
> 기존코드 포스팅 : [https://velog.io/@van1164/KoPring-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D-%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B5%AC%ED%98%84%ED%95%B4%EB%B3%B4%EA%B8%B0-5-%EC%97%85%EB%A1%9C%EB%93%9C-%EC%8B%9C%EA%B0%84-%EC%9D%B4%EA%B2%8C-%EC%B5%9C%EC%84%A0%EC%9D%BC%EA%B9%8C-feat.%EB%B9%84%EB%8F%99%EA%B8%B0-Non-Blocking](https://velog.io/@van1164/KoPring-%EC%8A%A4%ED%8A%B8%EB%A6%AC%EB%B0%8D-%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B5%AC%ED%98%84%ED%95%B4%EB%B3%B4%EA%B8%B0-5-%EC%97%85%EB%A1%9C%EB%93%9C-%EC%8B%9C%EA%B0%84-%EC%9D%B4%EA%B2%8C-%EC%B5%9C%EC%84%A0%EC%9D%BC%EA%B9%8C-feat.%EB%B9%84%EB%8F%99%EA%B8%B0-Non-Blocking)

### 적용 코드 <a href="#undefined" id="undefined"></a>

```kotlin
fun uploadVideoPartLast(fileUUID : String, totalChunk : Int): Flux<ServerSentEvent<String>> {
        //여러 part를 하나의 파일로 만들기
        val stopWatch = StopWatch()
        stopWatch.start("mp4로 만드는데 걸린 시간")
        val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
        runBlocking {
            Files.createFile(inputFilePath)
        }

        val videoFlux = Flux.range(0,totalChunk)
            .publishOn(Schedulers.boundedElastic())
            .flatMapSequential {
                val flux = uploadRepository.getPartByteArray(
                    bucketUrl,
                    fileUUID,
                    it
                )
                if(flux !=null){
                    Flux.just(flux)
                }
                else{
                    Flux.empty()
                }
            }
            .doOnNext{videoPart->
                logger.info{"파일write"}
                Mono.fromCallable {
                    Files.write(inputFilePath, videoPart, StandardOpenOption.APPEND)
                }.subscribeOn(Schedulers.boundedElastic()).subscribe()
            }


        stopWatch.stop()
        val m3u8Path = "$fileUUID.m3u8"
        val thumbNailPath = UUID.randomUUID().toString() + ".jpg"

        val deleteChunkFileFlux =
            Flux.range(0,totalChunk).flatMap{
                Flux.just(
                    uploadRepository.deletePart(fileUUID, it)
                )
            }
                .subscribeOn(Schedulers.boundedElastic())

        val thumbNailFlux = Mono.zip(Mono.just(inputFilePath),Mono.just(thumbNailPath))
            .flatMap{
                Mono.just(createThumbNail(it.t1,it.t2)).subscribeOn(Schedulers.parallel())
            }
        val saveVideoFlux = Mono.zip(Mono.just(fileUUID),Mono.just(thumbNailPath))
            .flatMap { Mono.just(saveThumbnailData(it.t1,it.t2)).subscribeOn(Schedulers.parallel()) }

        val mp4ToHlsFlux = Mono.zip(Mono.just(inputFilePath),Mono.just(m3u8Path),Mono.just(fileUUID))
            .flatMap{ Mono.just(mp4ToHls(it.t1,it.t2,it.t3)).subscribeOn(Schedulers.parallel())}

        return Flux.concat(videoFlux,Flux.merge(deleteChunkFileFlux,thumbNailFlux,saveVideoFlux,mp4ToHlsFlux))
        
```

### 실행 결과 (에러 발생😂) <a href="#undefined" id="undefined"></a>

#### 브라우저 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/ea659578-6695-4299-b91c-c134558e2b96/image.png)

#### 서버 로그 <a href="#undefined" id="undefined"></a>

```
 org.springframework.web.context.request.async.AsyncRequestTimeoutException
```

> 비동기로 처리를 했더니 다음과 같은 오류에 직면하였다. 오류를 보니 timeout이 발생한 것으로 보인다. 그래서 찾아보니 spring에서 `spring.mvc.async.request-timeout=시간`을 통해 **`timeout`**을 늘릴 수있다고 했다.\
> 하지만, 이걸 늘리는건 근본적인 해결법이 아니라고 생각했다. **`timeout`**을 늘린다고 해도 오류만 발생하지 않을 뿐이지 사용자가 기다려야하는 시간은 길어지고, 나중에 해상도 처리와 같은 로직을 추가하게 된다면 늘린 **`timeout`**을 더 늘려야하는 상황이 생길 것이라고 생각했기 때문이다.

***

### TimeOut을 근본적으로 해결할 방법 <a href="#timeout" id="timeout"></a>

> 기존의 REST API는 Response가 하나이며 그 Response가 지정된 TIMEOUT안에 돌아오지 않으면 응답이 끊어진다.\
> 하지만 큰 용량의 동영상을 업로드하고 처리하는 과정은 아무리 최적화를 하더라도 오래걸릴 수 밖에 없다.
>
> **TimeOut을 해결하려면**
>
> 크게 WebSocket 과 SSE(Server Sent Event) 두가지가 있다.\
> 그 중 클라이언트에서는 정보를 보낼 필요가 없기 때문에 SSE를 선택하였다.\
> (Polling과 Long Polling도 있지만... 필요한 기능에 비해 리소스 낭비가 크다고 생각했다.)

### SSE <a href="#sse" id="sse"></a>

> SSE는 서버의 데이터를 실시간으로, 지속적으로 **Streaming** 하는 기술이다.\
> SSE는 웹소켓과 달리, Client가 Server로부터 데이터만 받을 수 있는 방식이다.\
> SSE는 별도의 프로토콜을 사용하지 않고 HTTP 프로토콜만으로 사용할 수 있기 때문에 구현이 용이하다.

![](https://velog.velcdn.com/images/van1164/post/df560b88-4bb5-4906-a822-e0f17df3a091/image.png)

### Sink <a href="#sink" id="sink"></a>

> **Sinks란?**\
> Reactor에서 Processor(Publisher + Subscriber) 을 개선한 구현체.\
> Signal을 프로그래밍 방식으로 푸시\
> 멀티 스레드 방식으로 Signal을 전송해도 스레드 안정성 보장\
> Sink에 대한 포스팅은 따로 정리할 예정이다.

#### SSE에서의 Sink <a href="#sse-sink" id="sse-sink"></a>

```kotlin
val sink = Sinks.many().multicast().onBackpressureBuffer<Event>()

return sink.asFlux() // sink를 반환

sink.tryEmitNext(Event(이벤트명 ,메시지)) // 이벤트 방출
```

![](https://velog.velcdn.com/images/van1164/post/8acd9953-884c-4dfc-a715-e3368891c25c/image.png)

#### SSE 적용 <a href="#sse" id="sse"></a>

```kotlin
val sink = Sinks.many().multicast().onBackpressureBuffer<Event>()
fun uploadVideoPartLast(fileUUID : String, totalChunk : Int): Flux<ServerSentEvent<String>> {
    //여러 part를 하나의 파일로 만들기
    val stopWatch = StopWatch()
    stopWatch.start("mp4로 만드는데 걸린 시간")
    val inputFilePath = Paths.get(UUID.randomUUID().toString() + ".mp4")
    runBlocking {
        Files.createFile(inputFilePath)
    }

    val videoFlux = Flux.range(0,totalChunk)
        .publishOn(Schedulers.boundedElastic())
        .flatMapSequential {
            val flux = uploadRepository.getPartByteArray(
                bucketUrl,
                fileUUID,
                it
            )
            if(flux !=null){
                Flux.just(flux)
            }
            else{
                Flux.empty()
            }
        }
        .doFirst {
            sink.tryEmitNext(Event("ing" ,"파일 업로드 완료하는 중.."))
        }
        .doOnNext{videoPart->
            logger.info{"파일write"}
            Mono.fromCallable {
                Files.write(inputFilePath, videoPart, StandardOpenOption.APPEND)
            }.subscribeOn(Schedulers.boundedElastic()).subscribe()
        }


    stopWatch.stop()
    val m3u8Path = "$fileUUID.m3u8"
    val thumbNailPath = UUID.randomUUID().toString() + ".jpg"

    val deleteChunkFileFlux =
        Flux.range(0,totalChunk).flatMap{
            Flux.just(
                uploadRepository.deletePart(fileUUID, it)
            )
        }.doOnComplete {
            sink.tryEmitNext(Event("ing","썸네일 생성중.."))
        }
            .subscribeOn(Schedulers.boundedElastic())

    val thumbNailFlux = Mono.zip(Mono.just(inputFilePath),Mono.just(thumbNailPath))
        .flatMap{
            Mono.just(createThumbNail(it.t1,it.t2)).subscribeOn(Schedulers.parallel())
        }
    val saveVideoFlux = Mono.zip(Mono.just(fileUUID),Mono.just(thumbNailPath))
        .flatMap { Mono.just(saveThumbnailData(it.t1,it.t2)).subscribeOn(Schedulers.parallel()) }

    val mp4ToHlsFlux = Mono.zip(Mono.just(inputFilePath),Mono.just(m3u8Path),Mono.just(fileUUID))
        .flatMap{ Mono.just(mp4ToHls(it.t1,it.t2,it.t3)).subscribeOn(Schedulers.parallel())}.doFirst { sink.tryEmitNext(Event("ing","파일 변환 처리중...")) }

    Flux.concat(videoFlux,Flux.merge(deleteChunkFileFlux,thumbNailFlux,saveVideoFlux,mp4ToHlsFlux))
        .doOnComplete {
            sink.tryEmitNext(Event("finish",fileUUID))
        }
        .subscribe()
    return sink.asFlux().map{event->
        ServerSentEvent.builder<String>(event.message)
            .event(event.event)
            .build()
    }
```

### 결과 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/6c50611e-10ca-46c4-880d-e256393039dd/image.gif)

> **`Server Sent Event`** 를 통해서 **`WebFlux`** 의 스트림이 진행되는 중간마다 Event를 방출해서 업로드가 얼마나 진행되었는지, 현재는 어디까지 진행되었는지에 대한 이벤트를 방출할 수 있었다.\
> 처음에는 `TIMEOUT`만을 해결해보고자 적용한 **`Server Sent Event`** 였는데, 적용해보니 사용자에게 서버에서 일어나고 있는 상태를 알려주는 좋은 방식의 적용이었다고 생각한다.
>
> 추후에는 라이브 스트리밍도 구현을 계획중인데, **`Server Sent Event`**나 **`WebSocket`**을 사용한 실시간 채팅 서비스도 구현해볼 예정이다.

#### 😊 참고 <a href="#undefined" id="undefined"></a>

[https://why-doing.tistory.com/134](https://why-doing.tistory.com/134)\
[https://velog.io/@gwichanlee/SSE](https://velog.io/@gwichanlee/SSE)\
책 스프링으로 시작하는 리액티브 프로그래밍 \[저자: 황정식]
