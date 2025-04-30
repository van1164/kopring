# WebFlux로 실시간 채팅 SSE 만들기

## WebFlux로 실시간 채팅 SSE 만들기

_HTTP/1.1·HTTP/2부터 HTTP/3(QUIC) 전환, 하트비트 설계, 프록시-타임아웃까지 한 번에 정리_

> **목표**
>
> * Spring WebFlux 기반 채팅 서비스를 **SSE(Server-Sent Events)** 로 구현한다.
> * HTTP/1.1·2·3 어디서도 **장시간 끊김 없이** 유지된다.
> * 클라이언트 재접속, 로드밸런서 타임아웃, 모바일 NAT까지 **모두 고려**한다.

***

### 1. 왜 WebSocket 말고 SSE인가?

| 항목            | SSE                 | WebSocket            |
| ------------- | ------------------- | -------------------- |
| 네이티브 브라우저 API | `EventSource` 하나면 끝 | 별도 핸드셰이크, 프레임 파싱 필요  |
| 서버 구현         | HTTP 스트림만 뿌리면 됨     | 프레임 인코딩·Ping-Pong 구현 |
| **백프레셔**      | HTTP/2 → 자동         | 직접 프로토콜 설계           |
| 메시지 방향        | 서버 → 클라이언트 (단방향)    | 양방향                  |

\
보내는 경우에 비해 받는 경우가 대부분인 라이브 채팅방의 경우 단방향인 SSE로 받아오고 POST로 메시지 전송을 구현하는 것으로 설계

***

### 2. 기본 엔드포인트 설계

```kotlin
kotlin복사편집// ChatController.kt
@PostMapping                   // ① 채팅 전송
fun post(@RequestBody dto: ChatMessage): Mono<ChatMessage> =
    service.saveAndBroadcast(dto)

@GetMapping(                   // ② 실시간 수신
    path = ["/stream"],
    produces = [MediaType.TEXT_EVENT_STREAM_VALUE]
)
fun stream(@RequestParam roomId: String): Flux<ServerSentEvent<ChatMessage>> {

    val events = service.history(roomId)          // 과거 로그
        .concatWith(service.live(roomId))         // 새 메시지
        .map { m ->
            ServerSentEvent.builder(m)
                .id(m.id.toString())              // ← Last-Event-ID 복구용
                .event("chat")
                .build()
        }

    val heartbeats = Flux.interval(Duration.ofSeconds(10))
        .map { ServerSentEvent.builder<ChatMessage>()
                 .event("hb")                     // ← 하트비트 이벤트
                 .build()
        }

    return Flux.merge(events, heartbeats)         // 메시지 + 하트비트
}
```

* **`id:` 필드**를 넣어 두면 브라우저가 재접속할 때 `Last-Event-ID`를 보내 지난 메시지를 자동으로 이어받는다.
* **하트비트 10 초** : 프록시·NAT 타임아웃 리셋 용도. 메시지 버퍼에 기록되지 않으므로 재전송 대상 아님.

***

### 3. 전송층 이해: TCP ↔ QUIC(UDP)

| 구분    | HTTP/1.1·2                 | HTTP/3                          |
| ----- | -------------------------- | ------------------------------- |
| 기반    | **TCP**                    | **QUIC** (UDP + TLS + 신뢰·순서 보장) |
| 코드 수정 | 없음                         | 없음 — 전송층만 교체                    |
| 끊김 원인 | 프록시 idle\_timeout, 브라우저 버그 | 동일 + QUIC idleTimeout(디폴트 30 s) |

> **핵심**\
> QUIC은 “UDP니까 불안정”이 아니다. **TCP급 신뢰성**을 자체 제공한다.\
> 애플리케이션 코드는 그대로, Netty 설정만 바꾸면 된다.

***

### 4. Netty 서버에서 HTTP/3 켜기

1. **의존성 추가**

```kotlin
kotlin복사편집implementation("org.springframework.boot:spring-boot-starter-webflux")
runtimeOnly("io.netty.incubator:netty-incubator-codec-http3:0.0.29.Final")
```

2. **커스터마이저 작성**

```kotlin
@Component
class Http3Customizer : WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
    override fun customize(factory: NettyReactiveWebServerFactory) {
        factory.addServerCustomizers { http ->
            http.protocol(HttpProtocol.HTTP3)                     // ← 전송층 선택
                .http3Settings { s ->                             // QUIC 옵션
                    s.idleTimeout(Duration.ofMinutes(5))
                }
                .secure { spec ->                                  // TLS 필수
                    spec.sslContext(
                        Http3SslContextSpec.forServer(
                            /* KeyManagerFactory */, "password".toCharArray()
                        )
                    )
                }
        }
    }
}
```

3. **ALB/Nginx**
   * ALB: `idle_timeout.timeout_seconds = 900` (15 분 이상)
   * Nginx: `proxy_read_timeout 900s;` + `http3 on;`

***

### 5. 하트비트 설계 원칙

| 체크포인트     | 값             | 근거                                         |
| --------- | ------------- | ------------------------------------------ |
| 주기        | **10 초**      | ALB·Cloudflare 기본 60 s, 모바일 NAT 30 \~ 40 s |
| 페이로드      | `event: hb\n` | 10 byte 미만, 재전송 불필요                        |
| `id:` 포함? | **No**        | `Last-Event-ID`를 덮지 않도록                    |

트래픽 계산 : 10 B × 6 msg/min × 60 min = 3.6 KB/h — 사실상 무시 가능.

***

### 6. 브라우저 클라이언트 예시

```javascript
const es = new EventSource("/api/chat/stream?roomId=room1", { withCredentials: true });

es.addEventListener("chat", e => {
  const msg = JSON.parse(e.data);
  appendMessage(msg);
});

es.addEventListener("hb", () => {
  // 콘솔 로그만 남기고 실제 화면 갱신은 생략
});

es.onerror = () => {
  console.warn("SSE closed, retrying…");
};
```

> 크로스 도메인일 때는 CORS `Cache-Control: no-transform` 헤더를 빼먹지 않는다.

***

### 7. 운영 중 끊김을 막는 실전 체크리스트

1. **하트비트 주기** : 타임아웃보다 **짧게**.

***

### 8. 마이그레이션 팁 – HTTP/3 도입 순서

1. **동일 인스턴스**에서 HTTP/1.1·2·3 **동시 수용** (`protocols(HttpProtocol.HTTP11, HTTP2, HTTP3)`).
2. **클라이언트(브라우저)** 는 ALPN으로 자동 협상 — 실패 시 HTTP/2로 폴백.
3. **UDP 차단 네트워크** 테스트 → 끊김 없이 HTTP/2로 내려가면 완료.

***

### 9. 결론

* **SSE + WebFlux** 는 _단방향 채팅 스트림_ 을 가장 단순하게 만든다.
* HTTP/3(QUIC)로 옮겨도 **코드 수정 없이** 전송층 성능을 얻는다.
* 하트비트(10 s) + `id:` 필드만 지켜주면 **프록시·NAT·브라우저** 어떤 환경에서도 “안 끊기는” 실시간 채팅이 완성된다.

