# JUnit으로 동시성문제를 테스트할 수 있게 해보자! (라이브러리 개발기)

![](https://velog.velcdn.com/images/van1164/post/b6abea4d-2209-4a83-ab47-ced27943147f/image.png)

### 동시성 문제 <a href="#undefined" id="undefined"></a>

동영상 서비스 벡엔드 개발을 하면서 구독자수 좋아요수 댓글 수 등등 동시에 DB를 업데이트하면서 동시성 문제가 발생하는 경우가 많았다.

이제는 횟수를 다룰 때에는 동시성 문제가 주로 발생하겠구나 하고 예상하고 테스트를 해보겠지만, 또 어떠한 상황에서 동시성 문제가 발생하게 될지 알기 어렵다.

### 동시성 문제 테스트 <a href="#undefined" id="undefined"></a>

동시성 문제가 발생하는 지 확인하는 방법은 크게 2가지이다. **JUnit에서 멀티스레드를 통해 서비스계층에서 테스트해보는 방법**과 **K6나 nGrinder 등의 동시 트래픽 부하를 활용해 테스트해보는 방법**이다.

#### JUnit을 활용한 테스트 <a href="#junit" id="junit"></a>

예시

```java
public class CounterTest {

    @Test
    public void testCounterConcurrency() throws InterruptedException {
        int numberOfThreads = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
		CountDownLatch doneSignal = new CountDownLatch(numThreads);
        
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();
        
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() ->{
            	try {
                    likeService.like(user);
                    successCount.getAndIncrement();
                } catch (SoldOutException e) {
                    failCount.getAndIncrement();
                } finally {
                    doneSignal.countDown();
                }
            })
        }

        // 스레드들이 완료될 때까지 기다림
        doneSignal.await();
        executorService.shutdown();

        // 모든 스레드가 완료된 후의 좋아요수 확인
        assertEquals(successCount.get(), likeService.getLike());
    }
}
```

이러한 방법으로 테스트했을 때 장점은 JUnit을 활용해서 성공한 횟수와 데이터베이스에 업데이트된 값을 비교해서 빠르게 정합성의 문제가 발생하지 않은 것을 확인할 수 있다.

하지만 동시성 문제는 정합성도 중요하지만, 속도 또한 중요하다. 정합성문제가 발생하지 않도록 구현하면서, 응답속도도 최선이 되어야 하지만, JUnit만으로 테스트하기는 어렵다.

***

#### 동시 트래픽 부하를 활용한 테스트 <a href="#undefined" id="undefined"></a>

![](https://velog.velcdn.com/images/van1164/post/2142691c-8b10-4754-b5cc-07dc655e5ab1/image.png)

K6나 nGrinder 등을 사용해서 동시 트래픽을 부하하면서 테스트를 하면 위와 같이 성공한 요청수와 응답속도등을 쉽게 테스트해볼 수 있다.

하지만 이를 cmd나 부하 테스트 서비스에서 제공하는 화면에 들어가서 요청을 보내고 결과를 눈으로 확인하여야 한다. 테스트할 때마다 부하테스트를 하고 눈으로 성공한 요청 수를 확인하고 데이터베이스 값을 확인하여야한다.

***

### 라이브러리 개발 <a href="#undefined" id="undefined"></a>

위 두가지 방법들의 장점을 모두 발휘할 수 있도록 JUnit에서 부하테스트를 하고 응답속도와 정합성 등을 테스트코드로 관리할 수있는 라이브러리가 있으면 좋겠다고 생각했다. 하지만 아무리 찾아봐도 없었다. 그래서 내가 직접 만들어보기로 했다.

### K6 <a href="#k6" id="k6"></a>

![](https://velog.velcdn.com/images/van1164/post/d8f52faa-b564-4cf4-9153-9ce9498f9087/image.png)

K6는 커멘드라인에서 쉽게 실행이 가능하고 다양한 시나리오에 대해서 테스트해볼 수 있다. 그래서 이를 활용한 라이브러리를 개발하고자 생각하였다.

### K6Downloader <a href="#k6downloader" id="k6downloader"></a>

소스코드

```java
public class K6Downloader {

    private K6DownloaderByOS k6DownloaderByOS;
    private String k6BinaryPath;

    public K6Downloader(String downloadedPath, String addedK6Url) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        String k6Url = String.format("https://github.com/grafana/k6/releases/download/%s/%s",K6_VERSION,addedK6Url);
        if (os.contains("win")) {
            k6DownloaderByOS = new WindowsDownloader(k6Url,downloadedPath);
        } else if (os.contains("mac") || os.contains("darwin")) {
            k6DownloaderByOS = new MacDownloader(k6Url,downloadedPath);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            k6DownloaderByOS = new LinuxDownloader(k6Url,downloadedPath);
        } else {
            throw new Exception("Unsupported OS: " + os);
        }
    }

    public void downloadK6Binary() throws Exception {
        k6DownloaderByOS.k6DownloadAndExtract();
    }

}
```

K6가 로컬에 설치되어 있지 않아도 실행가능하도록 K6가 없으면 자동으로 설치해서 사용할 수 있게끔, `K6Downloader`를 구현하게 되었다. 다운로드는 OS에 따라 다운로드와 실행이 차이가 생기기 때문에 디자인패턴중에 전략패턴을 사용해서 OS별로 다른 다운로드 클래스를 적용하였다.

### K6Executor <a href="#k6executor" id="k6executor"></a>

```java
public class K6Executor {
	//생략
    public K6Result runTest() throws IOException, InterruptedException {
        String[] command = {k6BinaryPath, "run", scriptPath};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder outputString = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            outputString.append(line).append("\n");
        }
        reader.close();

        String result = outputString.toString();

        return resultToK6Result(result);
    }
}
```

개발자가 생성자에 넣은 스크립트파일 경로를 통해 K6를 실행시키고 그 결과를 분석해 K6Result 클래스로 만들어서 반환한다.

### K6Result <a href="#k6result" id="k6result"></a>

```java
public class K6Result {

    private String resultBody = null;
    private Boolean allChecksPass = false;
    private List<String> failedCheckList;
    private HttpReq httpReq;
	
    //생략
}
```

K6를 실행한 결과 클래스로 실패한 체크리스트와 체크리스트가 성공했는지, 요청성공 수, 실패 수, 실행결과 등등을 가지고 있다. 앞으로 더 많은 정보들을 추가할 예정이다.

### 실행 예시 <a href="#undefined" id="undefined"></a>

#### Run Test <a href="#run-test" id="run-test"></a>

```java
List<String> checkList = List.of("is status 200", "response time < 500ms");
K6Executor executor = new K6Executor("test.js",checkList);
K6Result result = executor.runTest();
```

#### HTTP 요청 수 조회 <a href="#http" id="http"></a>

```java
result.getTotalRequest()	// 전체 요청 수
result.getSuccessRequest()	// 성공한 요청 수
result.getFailRequest()		// 실패한 요청 수
```

#### 실행 결과 출력 <a href="#undefined" id="undefined"></a>

```java
result.printResult();
```

### Spring Boot 에서 사용하기 <a href="#spring-boot" id="spring-boot"></a>

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class K6Tests {

    @BeforeEach
    public void before() {
    }

    @Test
    void k6ExecutorTest() throws Exception {
        List<String> checkList = List.of("is status 200", "response time < 500ms");
        K6Executor executor = new K6Executor("test.js", checkList);
        try {
            K6Result result = executor.runTest();
            assertTrue(result.isAllPassed());	// 체크리스트 모두 성공 확인
            Trip trip = tripRepository.findById(tripId).get();
            // K6 성공 요청수와 좋아요 수를 비교 확인. 
            assertEquals(result.getSuccessRequest(),trip.getLikeCount());
        } catch (Exception e) {
            fail("Exception occurred during K6 load test: " + e.getMessage());
        }
    }

}
```

이를 활용하여 K6를 통해 실행한 요청 성공 횟수와 데이터베이스의 좋아요 수를 비교하여 정합성을 테스트하고, 체크리스트를 통해 응답 속도도 테스트할 수 있다.

이 라이브러리를 활용하면 로직을 수정하고도 바로 JUnit을 통해 정합성의 문제와 응답속도의 문제가 발생하는지 테스트해볼 수 있게 구현하였다.

### Maven 배포하기 <a href="#maven" id="maven"></a>

최소 기능 개발을 완료하고 MavenCentral에 배포를 하기로 결심했다.\
대부분의 블로그에서 Jira를 통해 이슈를 만드는 방법을 소개하고 있는데, 2024년부터 방법이 달라졌다.\
[https://central.sonatype.org/faq/what-happened-to-issues-sonatype-org/](https://central.sonatype.org/faq/what-happened-to-issues-sonatype-org/)\
그래서 이 사이트에서 제공하는 메일로 메일을 보냈고 다음과 같은 메일이 왔다.\
![](https://velog.velcdn.com/images/van1164/post/da103eac-ba6c-487a-badc-3c45d986f5bc/image.png)

[https://central.sonatype.org/publish-ea/publish-ea-guide/](https://central.sonatype.org/publish-ea/publish-ea-guide/)\
이 사이트가 시키는 대로 하면된다는 이야기였고 여기서 시키는 방법중에 Gradle 프로젝트를 배포하는 여러가지 방법이 있었지만, 나는 com.vanniktech.maven.publish 라이브러리를 사용하였다.

### Maven 로그인 <a href="#maven" id="maven"></a>

[https://central.sonatype.com/](https://central.sonatype.com/)\
위 사이트에 가입을 한다. github로 로그인할 경우 namespace를 만들어 줘서 편하다.

### GPG 다운로드 <a href="#gpg" id="gpg"></a>

중앙 저장소에 게시하기 위한 요구 사항 중 하나는 PGP로 서명되어야 한다는 것이다.\
[https://gnupg.org/download/index.html#sec-1-2](https://gnupg.org/download/index.html#sec-1-2)\
이 사이트에서 GPG를 다운로드 받고 GPG로 이 프로젝트를 서명함으로써, 신뢰된 프로젝트를 사용할 수 있게 한다.

#### 키 생성 <a href="#undefined" id="undefined"></a>

```
gpg --gen-key // 키 생성
gpg --list-keys // 생성된 키 확인 
```

```
pub   유효기간
      CSF332fDSFF05B4728190C4130ABA0F98    // 끝 8자리 = key Id
uid           [ultimate] Central Repo Test <central@example.com>
sub   rsa3072 2021-06-23 [E] [expires: 2023-06-23]
```

여기서 나온 값의 끝 8자리가 Key Id이다.

#### 키 전송 <a href="#undefined" id="undefined"></a>

공개 키를 공개 키 서버에 배포해야한다.

* keyserver.ubuntu.com
* keys.openpgp.org
* pgp.mit.edu

위 세가지 서버중에 하나로 보내면 되는데 내 경우에는 첫번째는 동작을 안했다. 그래서 keys.openpgp.org로 보냈다.

```
gpg --keyserver keys.openpgp.org --send-keys [keyId]
```

#### 시크릿 키 내보내기 <a href="#undefined" id="undefined"></a>

```
gpg --export-secret-keys -o secring.gpg
```

***

### Gradle 구성 <a href="#gradle" id="gradle"></a>

#### Gradle 파일 수정 <a href="#gradle" id="gradle"></a>

```groovy
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id 'java'
    id "com.vanniktech.maven.publish" version "0.28.0"
    id 'signing'
}

group = 'io.github.van1164'
version = '0.4.1'
sourceCompatibility = '11'
targetCompatibility = '11'

repositories {
    mavenCentral()
}
compileJava.options.encoding='UTF-8'
tasks.withType(JavaCompile){
    options.encoding = "UTF-8"
}

tasks.withType(Javadoc) {
    options {
        encoding 'UTF-8'
    }
}
signing {
    sign publishing.publications
}

mavenPublishing {
    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)


    coordinates("io.github.van1164", "k6-executor", "0.4.1")

    pom {
        name = "k6-executor"
        description = "k6-executor for junit"
        inceptionYear = "2024"
        url = "https://github.com/van1164/k6-executor-for-junit-test"


        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "van1164"
                name = "Sihwan Kim"
                url = "https://github.com/van1164"
            }
        }
        scm {
            url = "https://github.com/van1164/k6-executor-for-junit-test"
            connection = "scm:git:git://github.com/van1164/k6-executor-for-junit-test.git"
            developerConnection = "scm:git:ssh://git@github.com/van1164"
        }
    }
}




dependencies {
    testImplementation platform('org.junit:junit-bom:5.9.1')
    testImplementation 'org.junit.jupiter:junit-jupiter'
    implementation 'org.apache.commons:commons-compress:1.26.2'
}

test {
    useJUnitPlatform()
}
```

#### Maven Token <a href="#maven-token" id="maven-token"></a>

![](https://velog.velcdn.com/images/van1164/post/eecfc251-07b3-4630-8b2f-829fc6ce03e9/image.png)

View Account를 클릭하면 Generate User Token을 할 수 있다.

![](https://velog.velcdn.com/images/van1164/post/57eaacca-5f2a-4900-9a7a-cd9b3d303a31/image.png)\
여기서 OK를 누르면 UserName과 password가 나온다.

#### gradle.properties 수정 <a href="#gradleproperties" id="gradleproperties"></a>

고생을 많이 한 부분인데 프로젝트 위치가 아닌 OS root위치의 /.gradle/gradle.properties를 만들어서 넣어주어야 한다.

```
mavenCentralUsername=username
mavenCentralPassword=the_password

signing.keyId=12345678
signing.password=some_password
signing.secretKeyRingFile=/Users/yourusername/.gnupg/secring.gpg // gpg 위치
```

#### 명령 실행 <a href="#undefined" id="undefined"></a>

```
./gradlew publishAllPublicationsToMavenCentralRepository
```

![](https://velog.velcdn.com/images/van1164/post/a53fb113-8b09-4aea-a7d0-3b92c242ffde/image.png)

위 명령을 실행하면 Maven Central에 Publish에 정보가 Validation 정보가 올라오고 문제가 없으면 Publish를 누르면 된다.

### 라이브러리 확인 <a href="#undefined" id="undefined"></a>

[https://repo1.maven.org/maven2/](https://repo1.maven.org/maven2/)\
위 사이트에서 자신의 네임스페이스를 찾으면 배포된 라이브러리를 확인할 수 있다.\
![](https://velog.velcdn.com/images/van1164/post/6753ab66-e491-4470-af37-b38483edea03/image.png)

***

### 타 프로젝트에서 사용해보기 <a href="#undefined" id="undefined"></a>

### gradle에 추가 <a href="#gradle" id="gradle"></a>

```groovy
implementation 'io.github.van1164:k6-executor:0.4.1'
```

### 테스트 <a href="#undefined" id="undefined"></a>

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WebfluxSecurityExampleApplicationTests {

	@Test
	void contextLoads() throws Exception {
		List<String> checkList = List.of("is status 200", "response time < 500ms");
		K6Executor executor = new K6Executor("test.js",checkList);
		try {
			K6Result result =  executor.runTest();
			assertTrue(result.isAllPassed());
		} catch (Exception e) {
			fail("Exception occurred during K6 load test: " + e.getMessage());
		}
	}

}
```

타 프로젝트에서 Maven Central을 통해 라이브러리 사용이 가능해졌다!

***

### 결론 <a href="#undefined" id="undefined"></a>

프로젝트를 진행하면서 동시성 테스트를 K6를 통해서 진행하고, 데이터베이스를 확인하는 과정을 로직을 수정할 때마다 반복하였다. 물론, 실제 배포하는 환경과 로컬의 환경은 차이가 있기 때문에 응답속도에는 차이가 있을 수 있다. 그래서 응답속도의 기준을 좀 널널하게 가져가면서 정합성을 테스트할 필요가 있다.

실제 배포환경과 비슷한 환경을 만들어놓고 JUnit 테스트 코드 수정없이 스크립트의 URL만 변경해서 배포환경에서의 동시성 테스트가 가능하다.

### 깃허브 주소 <a href="#undefined" id="undefined"></a>

[https://github.com/van1164/k6-executor-for-junit-test](https://github.com/van1164/k6-executor-for-junit-test)

혹시 추가하고 싶은 기능은 이슈나 PR해주시면 확인하고 반영하겠습니다!!

### 참고 <a href="#undefined" id="undefined"></a>

[https://dami97.tistory.com/m/36](https://dami97.tistory.com/m/36)\
[https://olrlobt.tistory.com/90](https://olrlobt.tistory.com/90)\
[https://www.androidhuman.com/2017-05-21-common\_tips\_in\_gpg\_signing](https://www.androidhuman.com/2017-05-21-common\_tips\_in\_gpg\_signing)
