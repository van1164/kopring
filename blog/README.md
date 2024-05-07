# 동기 vs 비동기, Blocking vs Non-Blocking

## 동기와 비동기 <a href="#undefined" id="undefined"></a>

### Caller와 Calle <a href="#caller-calle" id="caller-calle"></a>

> **Caller** : 호출하는 함수\
> **Calle** : 호출당하는 함수

### 동기 <a href="#undefined" id="undefined"></a>

> 동기에서 **caller는 callee의 결과에 관심**이 있고 **caller가** 그 결과를 이용해서 **action을 수행**한다.

### 비동기 <a href="#undefined" id="undefined"></a>

> 비동기에서 **caller는 callee의 결과에 관심이 없으며** **callee가** 그 결과를 이용해서 **callback을 수행**한다.

![](https://velog.velcdn.com/images/van1164/post/01e410ba-1e03-4938-b514-4344e4d9ce71/image.png)

### Blocking과 Non-Blocking <a href="#blocking-non-blocking" id="blocking-non-blocking"></a>

### Blocking <a href="#blocking" id="blocking"></a>

> **caller**가 **callee**의 동작이 완료될 때까지 대기.\
> **제어권을 callee**가 가지고 있음.

### Non-Blocking <a href="#non-blocking" id="non-blocking"></a>

> **caller**가 **callee**를 기다리지않고 본인의 일을 함.\
> **제어권을 caller**가 가지고 있다.\
> 따라서 caller와 callee가 따로 동작하도록 별도의 **thread가 필요**하다.

![](https://velog.velcdn.com/images/van1164/post/20659a27-7689-4738-a71a-0827b26f2a45/image.png)
