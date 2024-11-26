# 실시간 스트리밍 구현(feat. Nginx Rtmp)

### 스트리밍 프로토콜 비교 <a href="#undefined" id="undefined"></a>

***

### RTMP <a href="#rtmp" id="rtmp"></a>

![](https://velog.velcdn.com/images/van1164/post/835824c8-e698-4c01-9bac-6baa180cfab5/image.png)

> 스트리밍 서버와 **Adobe Flash Player** 간에 오디오 및 비디오 파일을 전송하기 위해 **Adobe**에서 개발한 레거시 프로토콜

#### 장점 <a href="#undefined" id="undefined"></a>

**1. 짧은 지연시간**

**2. 많은 서비스에서 사용하고 있기때문에 Nginx Rtmp와 같은 활용가능한 라이브러리가 존재**

**3. 빨리 감기 및 되감기 가능**

#### 단점 <a href="#undefined" id="undefined"></a>

**1. 지연시간이 존재**

***

### WebRTC <a href="#webrtc" id="webrtc"></a>

![](https://velog.velcdn.com/images/van1164/post/f98dd18a-91e2-45ff-9e97-07f1f533df39/image.png)

> **WebRTC**(Web Real-Time Communication)는 웹 브라우저 간에 플러그인의 도움 없이 서로 통신할 수 있도록 설계된 API이다. 음성 통화, 영상 통화, P2P 파일 공유 등으로 활용될 수 있다.

#### 장점 <a href="#id-1" id="id-1"></a>

**1. 라이브러리가 다수 존재하여 개발에 용이**

**2. 지연시간이 거의 없는 실시간성**

#### 단점 <a href="#id-1" id="id-1"></a>

**1. 다른 프로토콜에 비해 많은 사용자를 받기 어려움**

***

### SRT <a href="#srt" id="srt"></a>

![](https://velog.velcdn.com/images/van1164/post/82589894-7652-4738-9faf-0f8314ba3d27/image.jpg)

> **SRT**는 Haivision이 개발한 오픈 소스 비디오 전송 프로토콜로, 공용 인터넷을 포함한 다양한 네트워크에서 지연 시간이 짧은 비디오 및 미디어 스트림을 제공하기 위해 두 개의 엔드포인트를 연결하도록 설계되었다.

#### 장점 <a href="#id-2" id="id-2"></a>

**1. 다른 프로토콜에 비해 보안이 높음**

**2. 대부분의 장치에서 호환이 가능함**

**3. 낮은 대기시간**

#### 단점 <a href="#id-2" id="id-2"></a>

**1. 다른 프로토콜에 비해 개발정보가 부족함**

***

### 프로토콜의 선택 <a href="#undefined" id="undefined"></a>

스트리머의 방송을 다수의 이용자가 시청을 하고 시청해야하기 때문에 **`WebRTC`** 는 적당하지 않다고 생각했다. 그래서 **`RTMP`**&#xC640; **`SRT`**&#xB97C; 고려해보았는데, **`SRT`**&#xC758; 정점이 **`RTMP`**&#xC5D0; 비해 좋아보이지만, 정보가 많지 않아서 **`RTMP`**&#xB85C; 구현하고 추후에 **`SRT`**&#xB97C; 고려해보는 것으로 생각했다.

***

### Nginx Rtmp <a href="#nginx-rtmp" id="nginx-rtmp"></a>

![](https://velog.velcdn.com/images/van1164/post/407d1efb-f691-4522-98b3-ac1864157aba/image.jpg)

#### nginx.conf 파일 구성 <a href="#nginxconf" id="nginxconf"></a>

```nginx
user root;
worker_processes auto;

events {
	worker_connections 768;
}
 
rtmp {
    server {
    # rtmp 포트 번호
        listen 1935;
        listen [::]:1935 ipv6only=on;

        chunk_size 4096;

        application live {
            live on;
            record off;

            #HLS로 저장
            hls on;
            hls_path /tmp/hls;
            hls_fragment 3;
            hls_playlist_length 60m;
			
            # 임시파일
			dash on;
            dash_path /tmp/dash;
        }
    }
}
```

**설정 변수들 참고**

[https://github.com/arut/nginx-rtmp-module/wiki/Directives](https://github.com/arut/nginx-rtmp-module/wiki/Directives)

#### nginx-rtmp 모듈을 설치한 nginx dockerfile <a href="#nginx-rtmp-nginx-dockerfile" id="nginx-rtmp-nginx-dockerfile"></a>

```docker
FROM alpine:3.13.4 as builder

RUN apk add --update build-base git bash gcc make g++ zlib-dev linux-headers pcre-dev openssl-dev

# nginx, nginx-rtmp-module 다운
RUN git clone https://github.com/arut/nginx-rtmp-module.git && \
    git clone https://github.com/nginx/nginx.git

# nginx를 설치하고 nginx-rtmp-module 추가
RUN cd nginx && ./auto/configure --add-module=../nginx-rtmp-module && make && make install

FROM alpine:3.13.4 as nginx

# ffmpeg 설치
RUN apk add --update pcre ffmpeg

# 빌드된 파일 및 설정파일 복사
COPY --from=builder /usr/local/nginx /usr/local/nginx
COPY nginx.conf /usr/local/nginx/conf/nginx.conf

# 실행
ENTRYPOINT ["/usr/local/nginx/sbin/nginx"]
CMD ["-g", "daemon off;"]
```

[https://github.com/arut/nginx-rtmp-module](https://github.com/arut/nginx-rtmp-module)

#### 이렇게 실행하고 OBS를 통해 rtmp서버 설정 후 방송을 시작하면 <a href="#obs-rtmp" id="obs-rtmp"></a>

![](https://velog.velcdn.com/images/van1164/post/019224e8-d86b-4e1d-bd99-8e5700d2c30f/image.png)

#### 아래와 같이 방송 파일들이 ts파일들로 나뉘어 저장된다. <a href="#ts" id="ts"></a>

![](https://velog.velcdn.com/images/van1164/post/d62e3373-acb7-4acf-89b1-0896782964e1/image.png)

***

### m3u8 접속 <a href="#m3u8" id="m3u8"></a>

#### nginx 설정 파일 추가 <a href="#nginx" id="nginx"></a>

```
http {
    server {
        listen 8088;

        root /tmp;

        location /hls {
            add_header 'Access-Control-Allow-Origin' '*' always;
            add_header 'Content-Type' 'application/json' always;

            types {
                 application/vnd.apple.mpegurl m3u8;
                 video/mp2t ts;
            }
        }
    }
}
```

#### 실시간 재생 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/25ef3a56-4893-4cb8-b289-002bf8adaa54/image.png)

***

### 동영상 저장에서 생긴 문제 <a href="#undefined" id="undefined"></a>

rtmp로 실시간으로 받은 영상을 로컬서버에 저장하는 것은 용량에 큰 문제가 생길 뿐아니라, 동영상에 대한 정보를 우리의 서버에도 기록을 해야 서비스가 원할하게 구현될 것이라고 생각했다.\
하지만, 서버나 S3에 동영상을 저장하는 방법을 여러가지 시도하였으나 모두 실패했다.

#### 실패 1. rtmp 스트림을 http를 통해 서버로 전송 <a href="#id-1-rtmp-http" id="id-1-rtmp-http"></a>

정말 다양한 방법을 통해 http를 통해 우리 서버로 스트림을 전송하고 내부에서 처리하고자 하였으나 rtmp를 http로 전송하는 방법이 존재하지 않았다.(내가 못찾은 것일수도..)

#### 실패 2. hls 폴더에 생성된 ts파일을 읽어와서 s3에 업로드 <a href="#id-2-hls-ts-s3" id="id-2-hls-ts-s3"></a>

hls폴더에 변화를 감지해서 변화가 생기면 파일들을 s3에 업로드를 구현하려했으나 ts파일은 한번에 작성되지않고 계속해서 업데이트 되기 때문에 실패했다.

#### 실패 3. RTMP에 대한 Listen을 서버에서 하도록 구현 <a href="#id-3-rtmp-listen" id="id-3-rtmp-listen"></a>

`ffmpeg -listen 1` 명령을 통해 rtmp응답을 직접 받아오는 것이 가능했지만 stream key가 정확하지 않아도 받아오는 문제가 있었고, 많은 정보조사를 했지만 이를 ffmpeg내에서 해결은 불가능했다.

**그래서 최종적인 방법은 nginx-rtmp와 서버를 동시에 사용하는 방법을 채택하였다. nginx-rtmp와 우리 서버가 각각 동작하지만 스트리밍 파일에 대한 디렉토리를 공유하는 방식으로 구현하였다.**

**docker compose**

```yaml
version: '1'
services:
  spring-app:
    image: van133/streaming:latest
    container_name: streaming
    ports:
      - "8080:8080"
    volumes:
      - tmp:/tmp
#    restart: unless-stopped


  mysql:
    image: mysql:latest
    container_name: db-mysql
    ports:
      - "3306:3306"
#    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ttink1245!
      TZ: Asia/Seoul

  nginx-rtmp:
    image: van133/nginx-rtmp
    ports:
      - "1935:1935"
    volumes:
      - tmp:/tmp

volumes:
  tmp:
```

tmp 폴더를 공유 volume으로 지정해서 우리의 서버에서 rtmp로 들어온 파일들을 체크할 수있게 구성하였다. 그 후 10분간 유효한 stream key에 대해서 m3u8 인덱스 파일이 생기면 이벤트를 방생시키는 동작을 구현하였다.

```kotlin
private fun checkStreamStart(m3u8Path: Path): Flux<Boolean> {
    return Flux.interval(Duration.ofSeconds(1))
        .take(600)
        .map {
            logger.info { m3u8Path }
            if (m3u8Path.exists()) {
                sink.tryEmitNext(Event("finish", "finish"))
                return@map true
            }
            else{
                return@map false
            }
        }
        .takeUntil{it == true}
}
```

### 결과 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/d976db78-21c3-4b82-810e-ca51ef151886/image.gif)

> 와.. 이번 구현은 `ffmpeg`,`rtmp`, `nginx`, `docker` 등등 배경지식을 매우 많이 필요로 했던 것같다. 이 포스팅에는 모든 내용을 담지 못했지만, 수 많은 시도를 했고 정말 힘들었다.
>
> 그냥 라이브 스트리밍만을 목표로 한다면, nginx-rtmp만으로도 가능하겠지만, 앞의 장면으로도 돌아갈 수있고 스트리머가 원한다면 저장할 수도 있는 서비스가 목표이기 때문에 조금 더 복잡했던 것 같다.하지만 그만큼 스트리밍과 rtmp프로토콜에 대해 깊게 공부할 수 있었다.

### 😋 참고 <a href="#undefined" id="undefined"></a>

[https://getstream.io/blog/streaming-protocols/](https://getstream.io/blog/streaming-protocols/)\
[https://corp.kaltura.com/blog/rtmp-server-guide/](https://corp.kaltura.com/blog/rtmp-server-guide/)\
[https://velog.io/@highway92/RTMP-%EC%99%80-WebRTC-%EB%B9%84%EA%B5%90](https://velog.io/@highway92/RTMP-%EC%99%80-WebRTC-%EB%B9%84%EA%B5%90)\
[https://velog.io/@happyjarban/WebRTC-%ED%8C%8C%ED%97%A4%EC%B9%98%EA%B8%B01-WebRTC-%EC%9D%B4%EB%A1%A0](https://velog.io/@happyjarban/WebRTC-%ED%8C%8C%ED%97%A4%EC%B9%98%EA%B8%B01-WebRTC-%EC%9D%B4%EB%A1%A0)\
[https://github.com/arut/nginx-rtmp-module/wiki/Directives](https://github.com/arut/nginx-rtmp-module/wiki/Directives)\
[https://qteveryday.tistory.com/371](https://qteveryday.tistory.com/371)\
[https://github.com/arut/nginx-rtmp-module/wiki/Directives](https://github.com/arut/nginx-rtmp-module/wiki/Directives)\
[https://ffmpeg.org/ffmpeg.html](https://ffmpeg.org/ffmpeg.html)
