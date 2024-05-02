## Scheduler

> #### 특정 설정을 하지 않으면 publisher는 subscribe를 호출한 caller의 쓰레드에서 실행된다.
#### Scheduler는 Publish나 Subscribe에서 어떤 쓰레드에서 실행할지 지정해줄 수 있다.

![](https://velog.velcdn.com/images/van1164/post/542d0753-90a3-4724-84a3-b120242ae69c/image.png)

 ### Immediate Scheduler

```kotlin
Flux.just(1,2,3,4)
	.subscribeOn(
    	Schedulers.immediate()
    )
    .subscribe(
    	logger.info("value: " + value);
    )

```
> subscribe를 호출한  쓰레드에서 **즉시 실행**한다.
즉, Schedulers를 사용하지 않은 것과 같다.

![](https://velog.velcdn.com/images/van1164/post/44fb4126-98f2-4528-ba90-0be16a7380d6/image.png)

---

### Single Scheduler
```kotlin
	Flux.just(1,2,3,4)
		.subscribeOn(
			Schedulers.single()
		).subscribe {
			logger.info{ "value : $it" }
		}
	Thread.sleep(1000)
```
> 캐싱된 1개의 쓰레드풀을 제공하며 **하나의 쓰레드**에서만 실행된다.

![](https://velog.velcdn.com/images/van1164/post/cbf816c7-6c6c-4254-8bde-9550ca9a0cef/image.png)

---

### Parallel Scheduler
```kotlin
	for(i : Int in 0..100){
		val idx = i
		Flux.create{sink ->
			sink.next(idx)
		}
			.subscribeOn(
				Schedulers.parallel()
			).subscribe {
				logger.info{ "value : $it" }
			}
	}
    Thread.sleep(1000);
```
![](https://velog.velcdn.com/images/van1164/post/cad1f1a9-97be-4913-a083-6ae389f74463/image.png)

> 캐싱된 **n개 크기**의 쓰레드풀을 제공한다. 기본적으로 CPU 코어 수
내 PC는 코어수가 6개다.

---

### BoundedElastic Scheduler
```kotlin
	for(i : Int in 0..30){
		val idx = i
		Flux.create{sink ->
			sink.next(idx)
		}
			.subscribeOn(
				Schedulers.boundedElastic()
			).subscribe {
				logger.info{ "value : $it" }
			}
	}
```


![](https://velog.velcdn.com/images/van1164/post/c4970e65-5890-462f-9e35-5f1801ed85ef/image.png)

> 캐싱된 스레드풀이지만, 크기는 고정되지 않은다. 
재사용할 수 있는 **쓰레드가 있다면 사용**, **없으면 새로생성**
생성 가능한 스레드 수는 제한되어있다. (기본 cpu x10)

---
