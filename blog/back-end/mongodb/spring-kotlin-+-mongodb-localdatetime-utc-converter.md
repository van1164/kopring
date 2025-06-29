# Spring Kotlin + MongoDB에서 LocalDateTime이 다른 시간으로 저장되는 문제 해결기 – UTC Converter 적용

> `LocalDateTime` → `Date` 직렬화 과정에서 JVM 기본 타임존이 끼어들어 값이 밀리는 문제가 생겼다. `@WritingConverter`/`@ReadingConverter`로 **UTC 고정** 변환기를 등록해 깔끔하게 해결했다.

***

### 1. 문제 상황

* 스택 : **Spring Boot (Kotlin), Spring Data MongoDB, MongoDB Atlas**
* 증상  : 프론트엔드에서 `"2025‑06‑29T12:00"` 로 전송한 시간이 DB(BSON DateTime)에 **`2025‑06‑29T03:00Z`** 로 저장됨(KST ↔ UTC 간 9‑hour shift).
* 로그·통계를 UTC 기준으로 작성하다 보니, 저장시각이 뒤틀려 쿼리 결과가 엉키는 문제가 발생.

### 2. 원인 분석

| 타입                   | 타임존 정보      | 직렬화 방식                                                                  |
| -------------------- | ----------- | ----------------------------------------------------------------------- |
| `LocalDateTime`      | ❌ 없음        | Spring Data가 **JVM Default TZ**(예: Asia/Seoul +09:00)를 적용해 `Instant` 변환 |
| `Date`/BSON DateTime | ✅ UTC 내부 표현 | 밀리초(epoch) 기준으로 항상 UTC 보관                                               |

결국 **“타임존 없는 값” → “타임존 있는 값”** 전환 시점에 JVM 기본 타임존이 끼어들어 9시간 밀려 저장된다.

### 3. 해결 전략 – UTC 고정 Converter

#### 3‑1. Converter 구현

```kotlin
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date

/** LocalDateTime → Date (UTC 고정) */
@WritingConverter
class LocalDateTimeToDateConverter : Converter<LocalDateTime, Date> {
    override fun convert(source: LocalDateTime): Date =
        Date.from(source.atZone(ZoneOffset.UTC).toInstant())
}

/** Date → LocalDateTime (UTC 고정) */
@ReadingConverter
class DateToLocalDateTimeConverter : Converter<Date, LocalDateTime> {
    override fun convert(source: Date): LocalDateTime =
        source.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
}
```

#### 3‑2. Spring Data에 등록

```kotlin
@Configuration
class MongoConfig {
    @Bean
    fun customConversions(): MongoCustomConversions = MongoCustomConversions(
        listOf(
            LocalDateTimeToDateConverter(),
            DateToLocalDateTimeConverter()
        )
    )
}
```

> 📌 **주의** : 단방향(`@WritingConverter`)만 등록하면 조회 시점에 다시 JVM TZ가 섞여 버그가 재현된다. **읽기·쓰기 두 Converter를 반드시 세트로** 등록하자.

### 4. 검증 스크립트

```kotlin
val now = LocalDateTime.of(2025, 6, 29, 12, 0)
repository.save(Post(id = null, publishedAt = now))
println(repository.findAll().first().publishedAt)
// 출력 : 2025-06-29T12:00 (✅ 그대로 유지)
```

MongoDB Compass에서 raw value 확인 시 `2025-06-29T12:00:00.000Z` 로 보이지만, 이는 Compass 뷰어가 UTC 기준으로 표시하기 때문이다.

### 5. 운영 시 고려 사항

1. **DB 툴의 타임존 설정**  – Compass/Robo 3T에서 _Local_ 선택 시 시각이 달라 보일 수 있다.
2. **Jackson 직렬화**  – REST API로 `LocalDateTime`을 주고받는다면 `ObjectMapper` 기본 타임존을 `UTC`로 맞추거나 ISO‑8601 문자열(Offset 포함)로 내려주자.
3. **다른 절대 시각 필드**  – 시스템 이벤트·로그 타임스탬프는 애초에 `Instant` (혹은 MongoDB의 `Date`) 로 저장하면 변환기가 필요 없다.

### 6. 다른 시나리오 & 대안

| 요구                      | 추천 타입                                        | 설명                 |
| ----------------------- | -------------------------------------------- | ------------------ |
| 사용자 지역 시각 보존(생일 등)      | `LocalDate` 또는 `LocalDateTime` + `ZoneId` 컬럼 | 화면 표시용 데이터를 그대로 유지 |
| 국제 기준 절대 시각(예약 실행 시점 등) | `Instant`                                    | 러닝쿼리·정렬이 단순        |
| 타임존 변환이 필요 없는 일회성 값     | `String`(ISO8601)                            | 직관적이나 쿼리 불편        |

### 7. 회고

* **Converter 한 방**으로 문제를 해소했다.
* DB/애플리케이션/프론트엔드 모두 **명시적 UTC** 기준을 맞추자. “묵시적 기본값”은 언젠가 발목을 잡는다.
* 실서비스에서는 **로드 테스트** 시점도 확인‑로그가 일치하는지 꼭 검증하자.
