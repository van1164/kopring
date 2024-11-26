# 부하 테스트를 해보자 (feat. nGrinder)

![](https://velog.velcdn.com/images/van1164/post/53a75b49-31ec-4b14-86f4-389f732cff63/image.png)

> **`nGrinder`**&#xB780; 네이버에서 진행한 오픈 소스 프로젝트로 **서버의 부하 테스트**를 위한 도구이다.

#### nGrinder는 크게 `Controller`와 `Agent`로 이루어져있다. <a href="#ngrinder-controller-agent" id="ngrinder-controller-agent"></a>

* **Controller는 전반적인 웹인터페이스를 제공하며**
* **Agent는 대상 시스템에 부하를 주는 프로세스 및 스레드를 실행한다.**

***

### 환경구성 <a href="#undefined" id="undefined"></a>

[https://github.com/naver/ngrinder/releases](https://github.com/naver/ngrinder/releases)\
위 링크를 통해 설치하여 실행할 수도 있고, 도커컨테이너를 통해서도 가능합니다.

😁 저는 도커로 실행하였습니다.

#### nGrinder Container 실행 <a href="#ngrinder-container" id="ngrinder-container"></a>

```
$ docker pull ngrinder/controller
$ docker run -d -v ~/ngrinder-controller:/opt/ngrinder-controller --name controller \
-p 80:80 -p 16001:16001 -p 12000-12009:12000-12009 ngrinder/controller
```

#### nGrinder Agent 실행 <a href="#ngrinder-agent" id="ngrinder-agent"></a>

```
$ docker pull ngrinder/agent
$ docker run -d --name agent --link controller:controller ngrinder/agent
```

> 공식 문서는 Controller 컨테이너가 동작 중인 머신과 Agent 컨테이너를 구동하지 말것을 강력하게 권고합니다.\
> **여러 Agent들이 동작하다 보면, 부하를 발생시키는 머신의 자원을 모두 소모할 수 있기 때문입니다.**

😂 그럼에도 저는 컴퓨터도 한 대이고 EC2 프리티어를 다 써버려서.. 한 컴퓨터로 진행하였습니다.\
다른 EC2를 사용한다면 아래와 같이 사용하시면 됩니다.

```
$ docker run -v ~/ngrinder-agent:/opt/ngrinder-agent -d ngrinder/agent {controller-ec2-ip}:{controller-ec2-web-port}
```

***

**실행이 완료되었다면 localhost로 접속해보면**

![](https://velog.velcdn.com/images/van1164/post/4bbc106f-82a3-4cf8-b39c-1c43a18efce2/image.png)

**로그인창이 나오고 초기 아이디와 비밀번호는 admin입니다.**

![](https://velog.velcdn.com/images/van1164/post/483a7a5d-2dad-42d5-a2fd-cf57db56b2de/image.png)

**Performance Test와 Script가 있는데, 우선 Script를 작성하고 그걸 이용해서 Performance Test를 진행할 수있습니다.**

**Script는 크게 Groovy와 Jython으로 작성이 가능한데,**

**Groovy는 자바, Jython은 파이썬과 유사합니다.**

**저는 Groovy로 진행하였습니다.**

> Groovy는 처음써봐서 걱정을 많이 했는데.... 그냥 JAVA랑 똑같았다 😁😁😁

### Script작성 <a href="#script" id="script"></a>

```groovy
//import 생략
@RunWith(GrinderRunner)
class TestRunner {

    public static GTest test
    public static HTTPRequest request
    public static List<Cookie> cookies = []

    @BeforeProcess
    public static void beforeProcess() {
        HTTPRequestControl.setConnectionTimeout(300000)
        request = new HTTPRequest()
        test = new GTest(1, "아이피:8080")
        // Set header data
        grinder.logger.info("before process.")
    }

    @BeforeThread
    public void beforeThread() {
        test.record(this, "test")
        grinder.statistics.delayReports = true
        grinder.logger.info("before thread.")
    }

    @Before
    public void before() {
        CookieManager.addCookies(cookies)
        grinder.logger.info("before. init headers and cookies")
    }

    @Test
    public void test() {
        grinder.logger.info("TEST START")
        String uuid = UUID.randomUUID().toString()
        NVPair[] headers = [new NVPair("Content-Type", "multipart/form-data; boundary=-----1234")]
        request.setHeaders(headers)
        for(int i =0; i<7; i++){
            NVPair title = new NVPair("title", "test")
            NVPair chunkNumber = new NVPair("chunkNumber",i.toString())
            NVPair totalChunk = new NVPair("totalChunk","7")
            NVPair fileUUID = new NVPair("fileUUID",uuid)
            NVPair[] videoData = [title,chunkNumber,totalChunk,fileUUID]
            String fileName = "./resources/test.part" +i
            NVPair file = new NVPair("video",fileName)
            NVPair[] files = [file]
            def data = Codecs.mpFormDataEncode(videoData,files,headers)
            request.setHeaders(headers)
            grinder.logger.info(request.getProperties().toString())

            HTTPResponse response = request.POST("http://아이피:8080/api/v1/upload/videoPart",data)

            if (response.statusCode == 301 || response.statusCode == 302) {
                grinder.logger.warn("Warning. The response may not be correct. The response code was {}.", response.statusCode)
            } else {
                assertThat(response.statusCode, is(200))
            }
        }

        NVPair title = new NVPair("title", "test")
        NVPair chunkNumber = new NVPair("chunkNumber","7")
        NVPair totalChunk = new NVPair("totalChunk","7")
        NVPair fileUUID = new NVPair("fileUUID",uuid)

        NVPair[] params = [title,chunkNumber,totalChunk,fileUUID]
        NVPair[] files = new NVPair[0]
        def data = Codecs.mpFormDataEncode(params,files,headers)

        HTTPResponse response = request.POST("http://221.163.248.49:8080/api/v1/upload/videoPartLast",data)
        if (response.statusCode == 301 || response.statusCode == 302) {
            grinder.logger.warn("Warning. The response may not be correct. The response code was {}.", response.statusCode)
        } else {
            assertThat(response.statusCode, is(200))
        }
    }



}
```

**multipart로 요청을 하기 위해서 Content-Type을 헤더에 지정해줬다.**

```groovy
NVPair[] headers = [new NVPair("Content-Type", "multipart/form-data; boundary=-----1234")]
```

> boundary에서 오류가 난다면\
> [https://gist.github.com/ihoneymon/a83b22a42795b349c389a883a7bbf356](https://gist.github.com/ihoneymon/a83b22a42795b349c389a883a7bbf356)\
> 이 github를 참고하시면 도움이 될겁니다 😊

**파일 전송방법**

> script만들었던 곳에서 resources라는 폴더를 만들고 거기에 파일들을 넣는다. 그 후 file의 위치를 아래 코드처럼 넣어주면 된다.

![](https://velog.velcdn.com/images/van1164/post/96a1ef0b-1de8-4582-a5ec-b6cb906963e4/image.png)

```groovy
String fileName = "./resources/test.part" +i
NVPair file = new NVPair("video",fileName)
NVPair[] files = [file]
```

**form형식으로 데이터 보내기**

```groovy
NVPair[] params = [title,chunkNumber,totalChunk,fileUUID]
NVPair[] files = new NVPair[0]
def data = Codecs.mpFormDataEncode(params,files,headers)
```

**이렇게 Script를 작성하였고 Validate를 통해 한번에 요청이 성공하는지 확인할 수있다.**

### 부하 테스트 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/05273ea0-e15e-4b93-b8d3-97f04b12f914/image.png)

> 이 창에서 테스트 이름과 user수 지속시간 등등을 지정하고 시작할 수 있다.

**부하테스트는 로컬 PC의 한계때문에 100명의 사용자가 동시에 접속했을 경우에 대해 10분간의 상황을 각각 비교 진행하였습니다.**

### 결과 <a href="#undefined" id="undefined"></a>

#### 기존의 동기적인 방식 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/7a750892-ea27-4096-b9f5-09feb91b409b/image.png)

![](https://velog.velcdn.com/images/van1164/post/c36ef870-fa16-458c-b87f-1e1cac3a5a59/image.png)

#### 비동기 방식 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/bcff8dba-3586-450c-bfee-6f118dbbf3d1/image.png)

![](https://velog.velcdn.com/images/van1164/post/83b9a8ea-58db-48f4-badf-f6fd573e77a1/image.png)

### 결론 <a href="#undefined" id="undefined"></a>

이전 포스팅에서 파일 업로드를 비동기로 처리하는 과정에서 업로드하는 시간을 비교하였을 때, 비동기적으로 처리한 서버가 20%정도 더 빠른 속도를 보였습니다.

장점만 존재하지 않을 것이라고 생각을 하였고, 많은 사용자가 들어왔을 때 수 많은 스레드의 사용이 병목현상을 불러올 것이라고 생각했습니다.

하지만, 예상과 정반대로 기존의 방식 평균응답시간이 2배이상 긴 것을 확인하였습니다. 오히려 비동기처리가 동시에 들어오는 트래픽이 많을 때에도 응답을 훨씬 빨리 반환하였습니다.

#### 아쉬움 <a href="#undefined" id="undefined"></a>

100명이 동시에 동영상을 업로드하는 것도 로컬PC에는 나름 극한의 상황이지만, 실제 서비스라면 더 많은 트래픽이 들어올 것이고 그러한 트래픽에서 어느정도 동작할 수 있는 서비스일지 궁금하지만... 시도해보려면 EC2 요금을 많이 내고 알아봐야겠지... 나중에 돈이 생기면 해보고자 합니다..

### 😊참고 <a href="#undefined" id="undefined"></a>

[https://naver.github.io/ngrinder/](https://naver.github.io/ngrinder/)\
[https://techblog.woowahan.com/2572/](https://techblog.woowahan.com/2572/)\
[https://notspoon.tistory.com/48](https://notspoon.tistory.com/48)\
[https://2021-pick-git.github.io/nGrinder-basic/](https://2021-pick-git.github.io/nGrinder-basic/)\
[https://jmdwlee.tistory.com/49](https://jmdwlee.tistory.com/49)\
[https://gist.github.com/ihoneymon/a83b22a42795b349c389a883a7bbf356](https://gist.github.com/ihoneymon/a83b22a42795b349c389a883a7bbf356)\
[https://velog.io/@hellonayeon/nGrinder-install-and-how-to-use-memo](https://velog.io/@hellonayeon/nGrinder-install-and-how-to-use-memo)
