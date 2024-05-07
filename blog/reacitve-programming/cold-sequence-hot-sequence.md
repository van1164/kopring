# Cold Sequence와 Hot Sequence

## Reactive Streams를 공부해보자! <a href="#reactive-streams" id="reactive-streams"></a>

### ❓ Publisher 와 Subscriber <a href="#publisher-subscriber" id="publisher-subscriber"></a>

![](https://velog.velcdn.com/images/van1164/post/fd1e5a87-fde7-49ec-8715-0d4f7aa34557/image.png)

#### 💧 Publisher : 특정 조건이나 이벤트가 발생했을 때 메시지를 보내는 주체 <a href="#publisher" id="publisher"></a>

#### ☂ Subscriber : Publisher에 의해 전송된 메시지중에서 조건에 맞게 필터링된 메시지를 받아서 처리하는 주체 <a href="#subscriber-publisher" id="subscriber-publisher"></a>

***

### ❓ Cold Sequence와 Hot Sequence <a href="#cold-sequence-hot-sequence" id="cold-sequence-hot-sequence"></a>

![](https://velog.velcdn.com/images/van1164/post/82eb1f2a-6c32-41a8-9810-9a8a8f6385af/image.png)

**1,2,3,4 를 차례대로 publish하는 Publisher가 있다고 가정하자.**

![](https://velog.velcdn.com/images/van1164/post/943497e6-755b-444e-81a1-3defc84dc7e4/image.png)

**1이 publish된 이후에 Subscriber1가 Subscribe한다.**

**3이 publish된 이후에 Subscriber2가 Subscribe한다.**

### 1. ❄ Cold Sequence <a href="#id-1--cold-sequence" id="id-1--cold-sequence"></a>

> **🔂 Subscriber가 Subscribe하는 시점에서 처음부터 지금까지 Publish된 과정을 다시 시작.**

![](https://velog.velcdn.com/images/van1164/post/f91807cb-9de5-4153-ad43-5f38feb62daf/image.png)

**위와 같은과정에서 Subscriber1과 Subscriber2 모두 1부터 4까지 모두 Publish받게 된다.**

### 2. 🔥 Hot Sequence <a href="#id-2--hot-sequence" id="id-2--hot-sequence"></a>

> **🔂 Subscriber가 Subscribe하는 시점에서부터 Publish된 것만을 Subscribe.**

![](https://velog.velcdn.com/images/van1164/post/e45dfa17-ec5d-440c-b967-d0e6f7f9b6d2/image.png)

**위와 같은과정에서 Subscriber1은 2,3,4를 Subscriber2는 4를 Publish받게 된다.**

> 다음으로는 **Reactor**에서의 **Publisher**와 **Subscriber**를 공부해볼 예정이다.
