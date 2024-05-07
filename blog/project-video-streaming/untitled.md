# 배포하면서 발생한 문제들.

![](https://velog.velcdn.com/images/van1164/post/1d99a85f-bf95-4d1a-9c1d-f7dcfb5604af/image.gif)

### 구상한 배포 아키텍쳐 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/b55c20f2-3e17-429f-bf10-039d260d925c/image.png)

> 앞서 구현했던 서비스를 도커라이징해서 도커 컴포즈를 통해 배포를 진행하고 이를 Github Actions를 사용해서 자동화하는 계획을 세우고 진행하였습니다. 이 과정중에 발생한 일들을 작성하고자합니다.

### 발생한 문제 <a href="#undefined" id="undefined"></a>

#### 구글 OAuth로그인시 Redirect mismatch 발생 <a href="#oauth-redirect-mismatch" id="oauth-redirect-mismatch"></a>

![](https://velog.velcdn.com/images/van1164/post/9f047eee-c2cd-419d-a852-284e33e7748f/image.png)

처음에는 Google Redirect Uri를 잘못 설정했다고 생각해서 여러 방향으로 설정해보았는데 아무리 설정해도 문제가 해결되지 않았다. 그래서 근본적인 설정들을 뒤져보다가 nginx에서 proxy pass할 때 문제가 생긴 것임을 알아냈다.

**기존 nginx conf**

```
        location / {
            proxy_pass http://app;
            }
```

nginx는 클라이언트가 요청한 ip와 host 등등을 넘기지 않으면 서버가 리다이렉트할 때 리다이렉트할 ip와 host를 알 수있는 방법이 없기 때문에 리다이렉트가 불가능했다.

**변경후 nginx conf**

```
        location / {
            proxy_pass http://app;
            proxy_set_header Host $host; #클라이언트가 요청한 원래 호스트를 정의한다.
            proxy_set_header X-Real-IP $remote_addr; #실제 방문자 원격 IP 주소를 프록시 서버로 전달한다.
            proxy_set_header X-Forwarded-Proto $scheme;
            }
```

**이렇게 변경해주니 정상 동작하였다.**

***

### the ssl parameter requires ngx\_http\_ssl\_module <a href="#the-ssl-parameter-requires-ngx_http_ssl_module" id="the-ssl-parameter-requires-ngx_http_ssl_module"></a>

https를 적용하고자 할때 443 ssl 을 거치게 되는데 이때 ngx\_http\_ssl\_module이 필요했다. 하지만 기본 nginx에는 설치가 되어있지 않아서, nginx를 빌드할 때 `--with-http_ssl_module` 넣어주어서 해결하였다.

***

### crypto.randomUUID 문제 <a href="#cryptorandomuuid" id="cryptorandomuuid"></a>

html에서 새로운 UUID를 생성할 때 crypto.randomUUID()를 사용했는데, localhost에서는 잘 작동했으나 배포후에는 동작하지 않았다.. 그래서 UUID를 생성할 수있는 새로운 함수를 생성해서 해결했다.

***

### java.io.IOException: Broken pipe 문제 <a href="#javaioioexception-broken-pipe" id="javaioioexception-broken-pipe"></a>

유독 SSE (Server Sent Event)를 사용하는 코드에서 이러한 에러가 발생하였다. 이런 에러가 발생한 이후에는 다른 코드들은 정상적으로 동작하는데, 중간 동작이 완료되었을 때 클라이언트로 보내는 메시지가 전달되지 않는 현상이 발생하였다.

`Broken pipe` 에러에 대해서 찾아보니 클라이언트의 요청이 끊어졌을 때 발생하는 오류였다. SSE일 때만 끊어지는 것을 보니 SSE가 동작하지 않는 것 같았다. 그래서 알아보니 nginx에서 reverse proxy할 때는 http 1.0을 기본으로 하는데 http 1.0은 연결을 지속하고 있을 수 없었다...

그래서 http1.1과 http2 중에서 설정하고자 하였는데, 찾아보니 http1.1을 사용하면 SSE 연결이 6개로 제한되기 때문에 과감하게 http2를 적용해보기로 하였다.

nginx 빌드할때 `--with-http_v2_module` 를 넣어주고 conf 파일에서 http2를 설정해주었다. 그 후 테스트해보니 문제 없이 동작하였다.

```
server {
    listen 443 ssl http2;
```

### 배포 화면 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/d79af02f-3408-4d35-be4d-8fffe6f9fa09/image.gif)

> 이제 CI/CD구축과 기본적인 동영상스트리밍과 라이브 스트리밍이 구현되었으니 전체적인 에러에 대한 처리와 채팅, 댓글기능등을 통해 그럴싸한 사이트로 만들어 보는게 목표입니다.

#### 참고 <a href="#undefined" id="undefined"></a>

[https://gong-check.github.io/dev-blog/BE/%EC%96%B4%EC%8D%B8%EC%98%A4/sse/sse/](https://gong-check.github.io/dev-blog/BE/%EC%96%B4%EC%8D%B8%EC%98%A4/sse/sse/)\
[https://eehnuyh.tistory.com/25](https://eehnuyh.tistory.com/25)\
[https://acetes-mate.tistory.com/215](https://acetes-mate.tistory.com/215)
