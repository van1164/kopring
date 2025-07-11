---
icon: circle-exclamation
---

# SSE Client 400 에러

```kotlin
val ktorSSEClient = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = Long.MAX_VALUE // SSE는 timeout을 설정하지 않음
        }
        install(SSE) {
            showCommentEvents()
            showRetryEvents()
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                allowStructuredMapKeys = true
                // The "classDiscriminator" is specific to kotlinx.serialization; ensure compatibility.
                classDiscriminator = "contentType"
            })
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }
```

위와 같은 방법으로 ktor에 SSE를 사용할 수 있도록 설정하고,



```kotlin
ktorSSEClient.sse(host = SSE_BASE_URL, path = "/api/v1/chat/stream/$roomId",port = 443) {

    incoming.collect { event ->
        println(event)
        event.data?.let { Json.decodeFromString(ChatMessage.serializer(), it) }?.let {
            emit(it)
        }
    }
}
```

이렇게 메시지를 받아오는 코드를 작성하였다.\
\


## 발생한 문제

로컬 서버를 켜고 로컬 ip로 이벤트를 받아오는 것에는 전혀 문제가 없었다.

하지만, 이를 서버에 올렸더니

<figure><img src="../../../.gitbook/assets/image (1) (1).png" alt=""><figcaption></figcaption></figure>

이런 오류를 뱉는다...



#### 현재까지는 로드 밸런서에서 거부하는 것으로 파악하고 있다.

하지만, 이상한 점은 \
![](<../../../.gitbook/assets/image (1) (1) (1).png>)

포스트맨이나 curl명령으로 요청을 보낼 때는 이상이 없다는 것이다.\
\


## 해결

```kotlin
ktorSSEClient.sse(
    url,
    reconnectionTime = 3.seconds
) {
    headers {
        append(HttpHeaders.Accept, "text/event-stream")
        append(HttpHeaders.Connection, "keep-alive")
        append(HttpHeaders.ContentType, "application/json")
    }
    parameters {
        jwt?.let { append("token", it) }
    }

    incoming.collect { event ->
        event.data?.let { Json.decodeFromString(ChatMessage.serializer(), it) }?.let {
            send(it)
        }
    }
}
```

~~정확한 원인은 파악하지 못했지만~~, sse 구현된 부분을 찾아보니 url로 요청을 보내는 메소드를 제공해주고 있어서 적용해보니 해결되었다!



추가

```kotlin
// ❌ 443 포트로 평문 HTTP 요청 → LB가 400 반환
client.sse(
    host = "example.com",
    port = 443,
    path = "/api/v1/chat/stream/$roomId"
) { … }
```

이렇게 host만 입력할 경우 http로 전송하기 때문에 서버에서는 오류가 발생하고 로컬에서는 아무 문제 없었던것.



```kotlin
client.sse(
    scheme = URLProtocol.HTTPS.name,   // 또는 scheme = "https"
    host   = "example.com",
    port   = 443,                      // 생략 가능 (HTTPS 기본값)
    path   = "/api/v1/chat/stream/$roomId",
    reconnectionTime = 3.seconds
) {  /* incoming.collect { … } */ }
```

URL을 직접입력하거나, https schem을 명시적으로 작성해야 이러한 문제가 발생하지 않는다.
