---
icon: aws
---

# AWS Community Day2024

## 플랫폼 엔지니어링

개발자의 요구사항을 개발하는 분야.\


### Key Concenpts

#### 1. Self service& internal developer platform(IDP)

내부 개발자 플랫폼. \
개발자의 인조부조화를 낮출 수있는 플랫폼

#### 2. Platform as a Product

플랫폼을 내부제품으로 취급하고 엔지니어링 조직의 요구사항을 개발



### Benefits



### 1. 개발 생산성 향상

### 2. 플랫폼팀이 전문성을 가지고 플랫폼 구축 가능

#### 3. 신뢰성과 거버넌스

#### 4. 비용 절감



### BackStage [https://backstage.io/](https://backstage.io/)

스포티파이가 기여한 오픈소스 개발자포탈

소프트웨어 카탈로그 : 소프트웨어 자산 공유 관리

소프트웨어 템플릿 : 새프로젝트를 위한 도구&#x20;



백스테이지 기반으로 프로젝트 시작 템플릿을 만들어두고 사용해서 개발 쇼요시간을 줄이고 비용 절감.





## CloudFormation

유저가 템플릿을 작성한후 템플릿을 CloudFormation에 업로드

조직 내 계정에 StackSets를 배포할 권한이 있어서 자격증명에 관련한 번거로운 작업이 필요 없음.



***

## Kafka를 이용해 점진적인 DB마이그레이션을 해보자.



### Debezium Source Connector

올리브영에서 오프라인 DB와 온라인 DB를 마이그레이션할 필요가 있었음.

Oracle DB를 Debezium Source Connector과 MSK Connector에 연결해서 다른 DB로 연결

로그기반, 쿼리기반 방식 존재



***

## 혼자서도 만들 수 있는 클라우드 기반 장애 알람 서비스

### 장애 알림 내재화 문제

콘텐츠가 부족한 기존 장애 Slack 알람

빠른 알람 필요

### Amazon Connect

장애 발생시 트리거를 받아 장애 내용을 전달해 주는 방향 사용 가능

전화애 대한 재다이얼, 내용, 전달등등 다양한 시나리오 적용 가능

### Amazon End User Messaging

문자 메시지 전송 API



### Badrock사용

LLM을 사용해 텍스트 생성, 요약을 쉽게 구현.\
이를 활용해서 오류를 받아서 그대로 전달하는게 아니라 Badrock을 사용해서 어떤 문제이고 어떻게 해결해야하는지 같이 전달.



***

## API 시큐리티

### Monolithic -> MSA

단일 유저 엔드포인트에서 기능 단위로 수많은 유저 엔드포이트 노출\


### OWASP API Security



#### 1. Broken Object Level Authentication

API에서 타인의 정보 조회시도에도 응답한다면 문제가 발생

\[대안]

JWT등을 활용하여 요청자의 정보를 대조하는 등의 행위를 통해 권한 요청이 적절하게 인가된 것인지 확인. 또한 무작위 대입 공격을 방어필요.



#### 2. Broken Authentication

#### &#x20;인가되지 않은 사용자가 인증에 성공할 수 있음.

\[대안] Brute Forece를 막기 위해 로그인 시도 횟수 제한 필요

AWS WAF등을 사용해서 URI + IP로 제한하여 큰 효과를 볼 수 있음



#### 3. Broken Object Property Level Authentication

사용자 입력 대로 문제 발생 가능

\[대안]

필요한 속성만 선택햐여 받는 것이 필요



#### 4. Unrestricted Resource Consumption

만약 공격자가 비번 찾기 SMS에 10000 번 사용한다면? 과 같이 공격자의 공격



\[대안]

단ㄴ위 시간마다 실행 횟수를 제한 권고



#### 5. Broken Function Level Authentication

API에서 사용자가 인가되지 않는 API사용 하는 문제

\[대안]

동일 API에 대하여 사용자 별 기능 구현 수준 차이 구현 필요



#### 6. Unrestricted access to sensitive businss flows

열차 무료 취소가 가능한 점을 악용해서 열차 좌석을 모두 한사람이 예매해버린다면?



#### 7. SSRF 취약점에 의해 접근 권한

Server Side Request ㄹForgery

AWS환경에서 불필요한 경우는 IMDS 비활성화 권고



#### 8. Security Misconfiguration

SSH가 모두에게 오픈되는 문제.



#### 9. API 인벤토리가 관리되지 않음으로써 생기는 문제

Swagger나 admin페이지가 공개되어 들어갈 수 있다면 문제가 발생

\[대안]

인벤토리 관리시에는 단순히 API뿐만 아니라 파라미터, CORS수준등의 명세 수준까지 관리 필요



#### Unsafe consumption of apis

써드 파티에서 오는 데이터를 무조건 믿는 것은 문제.

예) 네이버 카카오 가 준 결제 연동 값은 그대로 믿어도 되겠지?\
\


### AWS의 보안

#### Amazon Q Developer

#### Copilot은 데이터를 수집하지만 Amazon Q Developer는 수집을 끌 수 있기 때문에 장점이 있음



#### Amazon Codeguru Reviewer

#### 코드 퀄리티나 보안 취약점을 확인해주는 서비스&#x20;

