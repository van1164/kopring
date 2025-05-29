# 딕셔너리 (Hash Map)

#### Swift Dictionary  정리

* **값 타입이며 Copy-on-Write 전략을 사용함.**
* **`[Key: Value]` 형식의 제네릭 컬렉션임.**
* **`Key` 는 `Hashable` 프로토콜을 준수해야 함.**
* **`Sequence`·`Collection` 을 채택하여 `map`, `filter`, `reduce` 등 고차 함수를 지원함.**

***

**1️⃣ 생성 & 초기화**

*   **리터럴로 즉시 선언 가능함.**

    ```swift
    let countries = ["kr": "대한민국", "us": "USA"]
    ```
*   **빈 딕셔너리를 만들 수 있음.**

    ```swift
    var scores = [String: Int]()   // 타입 추론
    var cache: [URL: Data] = [:]   // 리터럴 빈 딕셔너리
    ```
*   **시퀀스→딕셔너리 변환을 지원함.**

    ```swift
    let ids   = [10, 7, 30]
    let names = ["손흥민", "호날두", "말디니"]
    let players = Dictionary(uniqueKeysWithValues: zip(ids, names))
    ```
*   **그룹핑으로 바로 만들 수 있음.**

    ```swift
    let words = ["swift", "sony", "sky", "kotlin"]
    let grouped = Dictionary(grouping: words, by: { $0.first! })
    // ["s": ["swift","sony","sky"], "k": ["kotlin"]]
    ```

***

**2️⃣ 값 읽기·쓰기**

*   **옵셔널 서브스크립트를 사용함.**

    ```swift
    let captain = players[10]       // String?  → "손흥민"
    ```
*   **부재 시 기본값을 설정할 수 있음.**

    ```swift
    scores["kim", default: 0] += 1  // 없으면 0으로 삽입 후 +1
    ```
*   **updateValue(\_:forKey:)로 이전 값을 얻을 수 있음.**

    ```swift
    if let old = players.updateValue("Heung-min Son", forKey: 10) {
        print("기존 이름은 \(old)였음.")
    }
    ```
*   **값 삭제는 두 형태를 가짐.**

    ```swift
    players[7] = nil                     // 서브스크립트 삭제
    let removed = players.removeValue(forKey: 30) // "말디니" 반환
    ```

***

**3️⃣ 변환·병합 API**

*   **merge로 두 딕셔너리를 결합할 수 있음.**

    ```swift
    var a = ["kr": 1, "jp": 2]
    let b  = ["kr": 3, "us": 4]
    a.merge(b) { cur, new in new }   // 충돌 시 새 값을 채택
    // 결과: ["kr": 3, "jp": 2, "us": 4]
    ```
*   **mapValues와 compactMapValues 제공함.**

    ```swift
    let lengths = countries.mapValues { $0.count }      // ["kr":3,"us":3]
    let valid   = scores.compactMapValues { $0 > 0 ? $0 : nil }
    ```
*   **reduce(into:)로 누적 계산이 가능함.**

    ```swift
    let total = scores.reduce(into: 0) { $0 += $1.value }
    ```

***

**4️⃣ 반복(Iteration) 패턴**

*   **키와 값을 동시에 순회함.**

    ```swift
    for (code, nation) in countries {
        print("\(code) → \(nation)")
    }
    ```
*   **키만, 값만 개별 순회할 수 있음.**

    ```swift
    for k in countries.keys   { print(k) }
    for v in countries.values { print(v) }
    ```
*   **정렬된 순회를 지원함.**

    ```swift
    for (k, v) in countries.sorted(by: { $0.key < $1.key }) {
        print(k, v)
    }
    ```
*   **lazy 체이닝으로 성능을 최적화할 수 있음.**

    ```swift
    let bigKeys = countries.lazy
        .filter { $0.value.count > 3 }
        .map(\.key)
    ```

***

**5️⃣ 성능·메모리 관리**

*   **reserveCapacity로 버킷 리사이즈를 미리 방지함.**

    ```swift
    var logs = [Int: String]()
    logs.reserveCapacity(1_000_000)
    ```
*   **Copy-on-Write이므로 복사는 ‘변경 시점’에만 일어남.**

    ```swift
    var d1 = countries
    var d2 = d1          // 여기서는 메모리 공유
    d2["cn"] = "China"   // 이때 비로소 실제 복사 발생
    ```

***

**6️⃣ 검색·유틸리티 함수**

*   **contains(where:)로 조건 검색이 가능함.**

    ```swift
    let hasUSA = countries.contains(where: { $0.key == "us" })
    ```
*   **randomElement()로 무작위 요소를 얻을 수 있음.**

    ```swift
    if let pair = countries.randomElement() {
        print(pair)
    }
    ```

***

**7️⃣ 동시성 & 안전성**

*   **Sendable 타입이면 actor 밖에서도 안전하게 전달 가능함.**

    ```swift
    struct User: Sendable { let id: Int; let name: String }

    actor UserStore {
        private var cache: [Int: User] = [:]   // User는 Sendable임.
        func add(_ u: User) { cache[u.id] = u }
    }
    ```
