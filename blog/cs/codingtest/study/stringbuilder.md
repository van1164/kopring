# StringBuilder

## Java StringBuilder 코딩테스트에서 주로 사용하는 함수들과 예제 정리

***

### 1. **StringBuilder 생성**

```java
javaCopy codeStringBuilder sb = new StringBuilder(); // 빈 StringBuilder 생성
StringBuilder sbWithString = new StringBuilder("Hello"); // 초기값을 가진 StringBuilder 생성
```

***

### 2. **문자열 추가: `append()`**

문자열의 끝에 새로운 문자열을 추가합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello");
sb.append(" World");
System.out.println(sb); // Output: Hello World
```

***

### 3. **문자열 삽입: `insert()`**

특정 인덱스에 문자열을 삽입합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello World");
sb.insert(6, "Java ");
System.out.println(sb); // Output: Hello Java World
```

***

### 4. **문자열 삭제: `delete()`**

특정 범위의 문자열을 삭제합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello Java World");
sb.delete(6, 11); // 6번 인덱스부터 11번 인덱스 전까지 삭제
System.out.println(sb); // Output: Hello World
```

***

### 5. **특정 문자 삭제: `deleteCharAt()`**

특정 위치의 문자 하나를 삭제합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello World");
sb.deleteCharAt(5); // 5번 인덱스의 공백 삭제
System.out.println(sb); // Output: HelloWorld
```

***

### 6. **문자열 변경: `replace()`**

특정 범위의 문자열을 다른 문자열로 교체합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello World");
sb.replace(6, 11, "Java");
System.out.println(sb); // Output: Hello Java
```

***

### 7. **문자열 뒤집기: `reverse()`**

문자열을 뒤집습니다. (특히 회문 판별 문제에서 유용)

```java
javaCopy codeStringBuilder sb = new StringBuilder("abcdef");
sb.reverse();
System.out.println(sb); // Output: fedcba
```

***

### 8. **문자 추출: `charAt()`**

특정 위치의 문자를 반환합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello World");
char ch = sb.charAt(6);
System.out.println(ch); // Output: W
```

***

### 9. **길이 확인: `length()`**

`StringBuilder` 객체의 문자열 길이를 반환합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello World");
int len = sb.length();
System.out.println(len); // Output: 11
```

***

### 10. **용량 확인 및 조정: `capacity()`**

* `capacity()`는 `StringBuilder`의 현재 용량을 반환합니다.
* `ensureCapacity()`는 최소 용량을 보장합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder();
System.out.println(sb.capacity()); // Default: 16
sb.ensureCapacity(50); // 최소 용량을 50으로 설정
System.out.println(sb.capacity()); // Output: 50
```

***

### 11. **부분 문자열 추출: `substring()`**

`StringBuilder`에서 특정 범위의 문자열을 반환합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello World");
String subStr = sb.substring(6, 11);
System.out.println(subStr); // Output: World
```

***

### 12. **String으로 변환: `toString()`**

`StringBuilder` 객체를 `String`으로 변환합니다.

```java
javaCopy codeStringBuilder sb = new StringBuilder("Hello World");
String str = sb.toString();
System.out.println(str); // Output: Hello World
```

***

### 코딩 테스트에서의 활용 예제

#### **1. 문자열을 반복적으로 이어붙이기**

```java
javaCopy codepublic class Main {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append(i).append(" ");
        }
        System.out.println(sb.toString().trim()); // Output: 1 2 3 4 5
    }
}
```

***

#### **2. 회문 판별**

```java
javaCopy codepublic class Main {
    public static boolean isPalindrome(String s) {
        StringBuilder sb = new StringBuilder(s);
        return sb.toString().equals(sb.reverse().toString());
    }

    public static void main(String[] args) {
        System.out.println(isPalindrome("madam")); // Output: true
        System.out.println(isPalindrome("hello")); // Output: false
    }
}
```

***

#### **3. 문자열 압축**

```java
javaCopy codepublic class Main {
    public static String compressString(String s) {
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                count++;
            } else {
                sb.append(s.charAt(i - 1)).append(count);
                count = 1;
            }
        }
        sb.append(s.charAt(s.length() - 1)).append(count);
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(compressString("aaabbcccc")); // Output: a3b2c4
    }
}
```

***



