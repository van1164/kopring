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

