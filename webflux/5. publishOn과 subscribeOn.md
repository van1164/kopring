## publishOn과 subscribeOn
> **publishOn**과 **subscribeOn**을 이용해서 Scheduler를 어디에 배치하느냐를 정할 수 있다.

<br>

스트림이 publishOn을 거치면 그 후에 publishOn에 명시한 Scheduler가 적용된다.
subscribeOn은 지정하면 구독하는 시작부터 publsihOn을 만날 때까지 명시한 Scheduler가 적용된다.


<img src="https://velog.velcdn.com/images/van1164/post/39e53189-e3a8-4301-94fb-1f5d528df4fe/image.png" width="50%">

### 예시코드

```kotlin
	for(i : Int in 0..10){
		val idx = i
		Flux.create{sink ->
			sink.next(idx)
			logger.info{"start : ${idx}"}
		}
			.publishOn(Schedulers.single())
			.doOnEach {
				logger.info{"middle : ${it.get()}"}
			}
			.subscribeOn(
				Schedulers.boundedElastic()
			).subscribe {
				logger.info{ "end : $it" }
			}
	}
```

![](https://velog.velcdn.com/images/van1164/post/b1f0b3b7-0c95-4cee-af95-78f2b6c0db9e/image.png)

>subscribeOn에서 지정한 boundedElastic이 처음에 적용된다. <br>
그 후 publishOn에서 만난 single이 subscirbe까지 적용되는 것을 볼 수 있다.
