---
icon: globe
---

# CNAME이란?

### 1. CNAME이란?

#### 1.1 CNAME 레코드의 기본 개념

* **CNAME**(Canonical Name) 레코드는 도메인 네임 시스템(DNS)에서, 하나의 도메인을 또 다른 도메인(호스트명)으로 '별칭(Alias)' 형태로 연결해주는 역할을 한다.
* 예를 들어 `blog.example.com`을 `example-blog.s3-website-ap-northeast-2.amazonaws.com` 등 다른 호스트명으로 연결하고 싶다면, CNAME 레코드를 사용할 수 있다.
* **“이 도메인은 사실상 저 도메인을 가리키고 있어."** 라는 지시를 DNS에 기록하는 방식이라고 이해하면 된다.



### 2. AWS ACM에서 나오는 CNAME은 무엇인가?

#### 2.1 도메인 검증(DNS 검증)을 위한 CNAME

* **AWS Certificate Manager(ACM)**&#xC5D0;서 SSL 인증서를 발급받을 때, 가장 간단하고 권장되는 방법은 **DNS 검증(DNS Validation)이**다.
* 이 때, 인증서 요청을 진행하면, 아래와 같은 '검증용 레코드' 정보를 보여준다.
* 이 레코드는 “**본인이 해당 도메인의 DNS에 접근 가능하다**”는 사실을 증명하기 위해, AWS 측에서 요구하는 **임시 CNAME이**다.
* 사용자는 자신의 DNS 관리자(이 글에서는 Cloudflare)를 통해 위와 같은 CNAME 레코드를 추가해주면, AWS는 “DNS 설정을 맘대로 추가할 권한이 있다 → 실제 도메인 소유자”라고 인식하고 인증서를 발급한다.

#### 2.2 CNAME이 아닌 TXT 레코드로 검증하는 경우도 있음

* ACM에서는 `CNAME` 레코드 대신 `TXT` 레코드를 써서 검증할 수도 있다.
* 하지만 최근에는 CNAME 방식을 더 권장하며, 콘솔에서도 기본값으로 CNAME이 표시된다.

***

### 3. Cloudflare에서 ACM 검증용 CNAME 등록 시 주의 사항

* 일반적으로 **도메인 검증 레코드**는 Cloudflare의 프록시(오렌지 구름)를 거치면 제대로 검증되지 않는 문제가 발생할 수 있다.
* “DNS only” (회색 구름) 모드로 설정해두면, Cloudflare가 직접 관여하지 않고 순수 DNS 응답만 처리한다.

***

### 4. 실제로 “CNAME을 통해 ACM 인증서를 발급받는” 과정

아래는 **ACM 인증서 발급** ↔ **Cloudflare DNS 설정** 간소화 예시 프로세스입니다.

1. **ACM에서 인증서 요청**
   * [AWS 콘솔](https://console.aws.amazon.com/) > **Certificate Manager** 이동
   * `Request a certificate` 클릭
   * `Domain names` 섹션에 `example.com`(또는 `*.example.com`) 등 필요한 도메인 이름 입력
   * 검증 방식은 **DNS Validation** 선택
2. **도메인 검증에 필요한 CNAME 정보 확인**
   *   인증서 요청을 마치면 다음과 같은 CNAME값이 제공됨.

       ```makefile
       makefile복사Name: _xxxxxxxxxxxx.example.com
       Type: CNAME
       Value: _yyyyyyyyyyyy.acm-validations.aws
       ```
3. **Cloudflare DNS에 CNAME 레코드 추가**
   * **Cloudflare** 대시보드 로그인 → 해당 사이트 선택 → **DNS** 메뉴 이동
   * “Add Record” 버튼 클릭 → Type: `CNAME`
   * **Name**: `_xxxxxxxxxxxx` (ACM에서 제시한 대로)
   * **Target** (또는 Content): `_yyyyyyyyyyyy.acm-validations.aws`
   * **Proxy status**: “DNS only” (회색 구름)
   * 저장(Save)
4. **ACM에서 검증 확인**
   * CNAME 레코드를 추가하고 나면, 대개 **수 분 내**(최대 30분 이상 걸릴 수도 있음) AWS가 해당 레코드를 인식.
   * ACM 콘솔 상태가 `Pending validation`에서 `Issued`로 바뀌면 성공적으로 인증서가 발급된 것.

***

### 5. 요약 & 마무리

1. **CNAME의 개념**
   * 한 도메인 이름을 다른 호스트명으로 매핑해주는 DNS 레코드
   * AWS ACM에서는 `도메인 소유 증명`을 위해 임시 CNAME 레코드를 요구함
2. **ACM 인증서 발급 과정에서의 CNAME**
   * DNS 검증 방식을 사용할 경우, AWS가 지정하는 `_xxxx.example.com → _yyyy.acm-validations.aws` 형태의 CNAME 레코드를 Cloudflare DNS에 추가
   * 검증 완료 후 인증서 발급(AWS 콘솔에서 상태가 `Issued`로 바뀜)
3. **Cloudflare DNS 설정 시 주의**
   * Proxy Status를 “DNS only”로 해주어야 검증이 정상적으로 진행됨(주황 구름 → 회색 구름)
   * 검증 레코드를 등록한 이후에는 복잡한 작업 없이 자동으로 갱신(연장)됨
4. **ACM 인증서 사용**
   * 발급 후엔 AWS 리소스(ELB, CloudFront, API Gateway 등)에서 간단히 불러와 HTTPS 적용 가능
   * Cloudflare를 프록시로 쓰면서 원본 서버(또는 AWS 서비스)도 TLS(HTTPS) 적용을 원하는 경우, ACM 인증서로 “원본 서버 ↔ Cloudflare” 구간을 완벽하게 암호화할 수 있음(Full (strict) 모드).

결국 \*\*“CNAME을 통해 도메인 검증을 완료하여 ACM 인증서를 발급받는다”\*\*라는 것은,

* **ACM**이 “(CNAME 레코드를 마음대로 추가할 권한이 있는) 당신이 진짜 도메인 주인이다”라는 사실을 확인하기 위한 절차
* 그 결과 SSL 인증서가 발급되면, AWS 인프라(혹은 서버)에 안전하게 적용 가능
* Cloudflare와의 궁합도 문제 없으며(단, 검증 과정에서 Proxy 상태 off 필요), 이후엔 자동 갱신으로 유지보수 부담을 크게 줄일 수 있음
