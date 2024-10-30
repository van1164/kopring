# Reactive Manifesto

### ❓ Reactive Manifesto란 <a href="#reactive-manifesto" id="reactive-manifesto"></a>

> 소프트웨어 아키텍쳐에 대한 선언문으로 Reactive System의 특성을 강조한 가이드라인.

![https://www.reactivemanifesto.org/](https://velog.velcdn.com/images/van1164/post/3d17e165-dca1-4937-8ff6-4092c6a67862/image.png)

***

### Resoponsive <a href="#resoponsive" id="resoponsive"></a>

#### ➕ 요구사항 <a href="#undefined" id="undefined"></a>

**문제를 신속하게 탐지하고 효과적으로 대처**

**신속하고 일관성있는 응답 시간 제공**

**신뢰할 수 있는 상한선을 설정해서 일관된 서비스 품질을 제공해야함.**

#### ✅ 얻는 효과 <a href="#undefined" id="undefined"></a>

**오류 처리를 단순화하고 최종 사용자의 신뢰를 구축할 수 있음.**

***

### Resilient <a href="#resilient" id="resilient"></a>

#### ➕ 요구사항 <a href="#id-1" id="id-1"></a>

**복제, 봉쇄, 격리, 위임등을 실현해야함.**

> 구성 요소를 서로 격리함으로써 시스템 전체를 손상시키지 않고 시스템의 일부에 오류가 발생하고 복구될 수 있도록 보장하여야 한다.

#### ✅ 얻는 효과 <a href="#id-1" id="id-1"></a>

**장애에 직면하더라도 응답성을 유지시켜줌.**

***

### Elastic <a href="#elastic" id="elastic"></a>

#### ➕ 요구사항 <a href="#id-2" id="id-2"></a>

**중앙 집중적이지 않도록 설계**

**실시간 성능 측정 도구 필요**

**규모확장 알고리즘 지원**

#### ✅ 얻는 효과 <a href="#id-2" id="id-2"></a>

**작업량이 변화하더라도 응답성을 유지할 수 있다.**

***

### Message Driven <a href="#message-driven" id="message-driven"></a>

#### ➕ 요구사항 <a href="#id-3" id="id-3"></a>

**비동기 메시지통신을 통해 느슨한 결합을 유지.**

**Non-Blocking으로 통신해야 한다.**

#### ✅ 얻는 효과 <a href="#id-3" id="id-3"></a>

**시스템 오버헤드를 줄일 수 있다.**

**유연성을 부여하며, 부하관리와 흐름제어를 가능하게 할 수 있다.**

***

### 🍲 정리 <a href="#undefined" id="undefined"></a>

> 요청에 대해서 즉작적으로 응답하고, 장애와 작업량 변화에 대응해서 **응답성을 유지**해야한다.\
> 이러한 목표를 비동기 **Non-Blocking**기반의 메시지 큐를 사용해서 통신한다.
