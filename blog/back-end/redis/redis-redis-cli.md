# Redis 파이썬 클라이언트와 Redis CLI에서 자주 쓰는 명령어 정리

Redis는 단순한 **Key-Value** 저장소로 유명하지만, **Streams**, **List**, **Pub/Sub** 등 다양한 자료구조를 지원해 광범위한 메시지 큐/이벤트 처리/캐싱 등에 활용된다.\
특히 최근에는 **Redis Streams**를 이용해 **메시지 큐** 또는 **이벤트 버스** 역할을 수행하는 경우가 많다.

본**파이썬 Redis 클라이언트(redis-py)를 사용**하면서 **Redis Streams** 및 **Redis List**를 중심으로, 코드 예시 및 redis-cli를 통한 명령어 사용법을 함께 정리한다.

***

### 2. Redis Streams 관련 명령어

#### 2.1. `XADD`

**스트림에 메시지를 추가**할 때 사용하는 명령어\
Redis Streams는 **Key(=Stream 이름)**, **Field-Value 쌍**으로 이루어진 메시지를 저장

**파이썬 예시**

```python
python코드 복사# 예: news:crawled 라는 스트림에 title, content 필드로 메시지 추가
redis_client.xadd("news:crawled", {"title": "Sample Title", "content": "Sample Content"})
```

* 스트림(키) 이름
* 필드-값 쌍

**redis-cli 예시**

```bash
# redis-cli에서 XADD 명령어
XADD news:crawled * title "Sample Title" content "Sample Content"
```

* `*`는 Redis가 자동으로 메시지 ID를 생성하도록 함.
* `title`와 `content`는 필드 이름, 뒤 문자열이 값.

***

#### 2.2. `XGROUP CREATE`

**Redis Streams의 Consumer Group**을 만들 때 사용하는 명령어.\
원하는 시점부터 소비하기 위해 **`MKSTREAM`**(키가 존재하지 않을 시 스트림 생성 옵션)과 `ID`(소비 시작 지점) 등을 지정할 수 있음.

**파이썬 예시**

```python
stream_name = "news:crawled"
group_name = "summarizer_group"

try:
    redis_client.xgroup_create(stream_name, group_name, id='0', mkstream=True)
except redis.exceptions.ResponseError as e:
    if "BUSYGROUP" in str(e):
        # 이미 그룹이 존재할 경우
        pass
```

**redis-cli 예시**

```bash
bash코드 복사XGROUP CREATE news:crawled summarizer_group 0 MKSTREAM
```

* `0` : 스트림의 처음(ID=0-0)부터 읽겠다
* `MKSTREAM` : 스트림 키가 없다면 생성

***

#### 2.3. `XREADGROUP`

**Consumer Group**에서 **메시지를 읽는** 명령어.\
&#xNAN;**`GROUP <group> <consumer>`** 구문으로 해당 그룹 및 컨슈머 이름을 지정해 사용할 수 있음.

**파이썬 예시**

```python
messages = redis_client.xreadgroup(
    groupname="summarizer_group",
    consumername="summarizer_1",
    streams={"news:crawled": ">"},  # ">"는 아직 소비되지 않은 최신 메시지를 의미
    count=10,
    block=5000
)
```

* `count=10`: 한 번에 최대 10개의 메시지 읽기
* `block=5000`: 5초(5000ms) 동안 대기

**redis-cli 예시**

```bash
XREADGROUP GROUP summarizer_group summarizer_1 COUNT 10 BLOCK 5000 STREAMS news:crawled >
```

* `>` : 새로 들어온 메시지를 기다렸다가 읽기

***

#### 2.4. `XACK`

**Consumer Group**에서 메시지를 처리 완료(ACK) 상태로 표시\*\*할 때 사용하는 명령어.\
이 과정을 통해 “해당 메시지가 정상적으로 처리되었다”고 Redis에 알리고, pending 상태에서 제거.

**파이썬 예시**

```python
# group_name, message_id가 있다고 가정
redis_client.xack("news:crawled", group_name, message_id)
```

**redis-cli 예시**

```bash
XACK news:crawled summarizer_group 1678712621123-0
```

* 메시지 ID(예: `1678712621123-0`)를 직접 지정해야 함.

***

### 3. Redis List 관련 명령어

#### 3.1. `RPUSH`

**리스트의 끝(오른쪽)에 데이터를 삽입**하는 명령어.\
스트림의 메시지를 요약한 결과 등을 임시로 보관할 때 리스트를 사용할 수 있음.

**파이썬 예시**

```python
summary = {"title": "Sample Title", "summary": "요약문..."}
redis_client.rpush("summaries:list", json.dumps(summary))
```

**redis-cli 예시**

```bash
RPUSH summaries:list "{\"title\":\"Sample Title\",\"summary\":\"요약문...\"}"
```

* 일반적으로 JSON을 문자열 그대로 넣을 수 있습니다.

***

#### 3.2. `LRANGE`

**리스트의 특정 범위를 조회**하는 명령어.\
**인덱스**는 0부터 시작하며, `-1`은 마지막 요소를 의미.

**파이썬 예시**

```python
items = redis_client.lrange("summaries:list", 0, 9)  # 0번째부터 9번째까지 (총 10개)
for item in items:
    data = json.loads(item)
    print(data)
```

**redis-cli 예시**

```bash
LRANGE summaries:list 0 9
```

***

#### 3.3. `LTRIM`

**리스트의 특정 구간만 남기고 나머지는 제거**하는 명령어.\
`LTRIM summaries:list 0 9` 라면 0\~9번 인덱스만 남김.

**파이썬 예시**

```python
# 0~49번 인덱스를 남기고 나머지 삭제
redis_client.ltrim("summaries:list", 0, 49)
```

**redis-cli 예시**

```bash
LTRIM summaries:list 0 49
```

***

### 4. 그 외 자주 쓰는 명령어 예시

#### 4.1. `GET` / `SET`

Redis의 가장 기본적인 **Key-Value** 형태로 문자열을 저장하고 읽을 때 사용.

**파이썬 예시**

```python
redis_client.set("mykey", "hello world")
value = redis_client.get("mykey")
print(value)  # b'hello world' (bytes 타입)
```

* Python Redis 클라이언트는 기본적으로 byte 문자열로 반환
* 필요시 `.decode("utf-8")` 등으로 디코딩해야 함

**redis-cli 예시**

```bash
SET mykey "hello world"
GET mykey
```

***



* **Streams**: 메시징/이벤트 용도로 유용 (XADD, XREADGROUP, XACK 등)
* **List**: 간단한 큐/스택/임시 저장소 형태로 활용 가능 (RPUSH, LRANGE, LTRIM 등)
* **DLQ**(Dead Letter Queue)나 확장된 시나리오에서는 해당 명령어 조합을 적절히 설계하여, 메시지 재처리나 에러 처리 로직을 안전하게 구성할 수 있다.
