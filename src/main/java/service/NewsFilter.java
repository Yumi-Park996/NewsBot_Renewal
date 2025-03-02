package service;  // 해당 클래스가 service 패키지에 속함을 명시

import model.NewsItem;  // 뉴스 아이템 데이터 모델 클래스 임포트

import java.util.List;  // List 컬렉션 클래스 임포트
import java.util.stream.Collectors;  // 스트림과 컬렉터 기능 임포트 (스트림 필터링 시 사용)

public class NewsFilter {  // 언론사 기준으로 뉴스 필터링하는 기능을 가진 클래스 정의

    // 허용된 언론사 리스트 (기본 설정된 언론사 8개)
    private static final List<String> DEFAULT_ALLOWED_PRESS = List.of(
            "매일경제", "한국경제", "헤럴드경제",  // 경제지 3개
            "이데일리", "파이낸셜뉴스", "연합뉴스",  // 경제/종합/통신사 3개
            "뉴스1", "뉴시스"  // 통신사 2개
    );

    // 주어진 뉴스 리스트에서 허용된 언론사의 기사만 필터링
    public List<NewsItem> filterByPress(List<NewsItem> newsItems) {
        System.out.println("필터링 전 기사 수: " + newsItems.size());  // 필터링 전 전체 기사 수 출력

        // 디버깅용으로 각 기사 정보와 필터링 결과 출력 (허용 언론사 여부 체크)
        for (NewsItem item : newsItems) {
            System.out.println("기사 제목: " + item.getTitle());  // 기사 제목 출력
            System.out.println("기사 링크: " + item.getoriginallink());  // 기사 원본 링크 출력
            System.out.println("언론사 매칭 결과: " +  // 해당 기사가 허용 언론사 기사인지 여부 출력
                    DEFAULT_ALLOWED_PRESS.stream().anyMatch(press ->  // 스트림으로 각 언론사 체크
                            item.getoriginallink().contains(getPressDomain(press))  // 원본 링크에 해당 언론사 도메인 포함 여부 확인
                    )
            );
        }

        // 스트림을 이용해 필터링
        List<NewsItem> filtered = newsItems.stream()
                .filter(this::isPressAllowed)  // 허용된 언론사인지 검사하는 메서드 적용
                .collect(Collectors.toList());  // 조건을 만족하는 기사만 리스트로 수집

        System.out.println("필터링 후 기사 수: " + filtered.size());  // 필터링 후 남은 기사 수 출력
        return filtered;  // 필터링 결과 반환
    }

    // 특정 뉴스 아이템이 허용된 언론사의 기사인지 확인하는 메서드
    private boolean isPressAllowed(NewsItem item) {
        boolean isAllowed = DEFAULT_ALLOWED_PRESS.stream().anyMatch(press ->  // 허용된 언론사 목록 스트림 순회
                item.getoriginallink().contains(getPressDomain(press))  // 원본 링크에 언론사 도메인 포함 여부 확인
        );

        // 각 기사별 필터링 결과 디버깅 출력
        System.out.println("기사 제목: " + item.getTitle());
        System.out.println("기사 링크: " + item.getoriginallink());
        System.out.println("필터링 결과: " + isAllowed);

        return isAllowed;  // 필터링 결과 반환 (true: 허용된 언론사, false: 비허용)
    }

    // 언론사 이름을 기반으로 해당 언론사의 도메인을 반환하는 메서드
    private String getPressDomain(String pressName) {
        // switch 문을 사용해 언론사별 도메인 매핑
        return switch (pressName) {
            case "매일경제" -> "mk.co.kr";  // 매일경제 도메인
            case "한국경제" -> "hankyung.com";  // 한국경제 도메인
            case "헤럴드경제" -> "heraldcorp.com";  // 헤럴드경제 도메인
            case "이데일리" -> "edaily.co.kr";  // 이데일리 도메인
            case "파이낸셜뉴스" -> "fnnews.com";  // 파이낸셜뉴스 도메인
            case "연합뉴스" -> "yna.co.kr";  // 연합뉴스 도메인
            case "뉴스1" -> "news1.kr";  // 뉴스1 도메인
            case "뉴시스" -> "newsis.com";  // 뉴시스 도메인
            default -> "";  // 위에 해당하지 않는 경우 빈 문자열 반환 (비허용 언론사)
        };
    }
}

/*
```
## 1. Stream이란?

### 정의
`Stream`은 자바에서 데이터를 다룰 때, 특히 **컬렉션(List, Set 등)**이나 **배열**처럼 여러 데이터를 순차적으로 처리할 때 사용하는 도구다. 

데이터를 직접 반복문으로 처리하는 대신, **데이터의 흐름(스트림)을 따라가면서 필터링, 변환, 수집 등의 작업을 할 수 있게** 해준다.

---

## 2. 왜 쓰는가?

### 기존 방식의 문제점
- for문으로 데이터를 반복하면서 조건을 검사하고, 새로운 리스트에 담고, 정렬하고... 이런 과정이 반복된다.
- 데이터 흐름을 한눈에 파악하기 어렵다.
- 로직을 수정할 때 반복문 안의 로직을 일일이 고쳐야 한다.
- 데이터 처리 코드와 비즈니스 로직이 섞이면서 코드 가독성이 떨어진다.

### Stream 방식의 장점
- **선언적으로 데이터 흐름을 표현**할 수 있어서 가독성이 좋아진다.
- 원본 데이터를 변경하지 않고, 새로운 데이터를 반환하는 방식이라 **불변성**이 유지된다.
- 필터링, 매핑, 정렬, 집계 같은 작업을 메서드 체인 방식으로 이어 붙일 수 있어, **한눈에 데이터 흐름을 파악하기 쉽다**.
- 내부 반복을 사용해, **병렬 처리로 성능 개선 기회**도 있다.

---

## 3. 언제 쓰는가?

### 다음과 같은 경우 Stream이 유용하다
- **리스트에서 특정 조건을 만족하는 데이터만 추출할 때** (필터링)
- **리스트의 각 데이터를 다른 형태로 변환해야 할 때** (매핑)
- **리스트 데이터를 정렬할 때** (정렬)
- **리스트의 데이터를 하나로 합쳐야 할 때** (집계)

---

## 4. Stream과 기존 방식 비교

### 예) 리스트에서 짝수만 골라내기

#### 기존 방식 (for문)
```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6);
List<Integer> evenNumbers = new ArrayList<>();
for (int number : numbers) {
    if (number % 2 == 0) {
        evenNumbers.add(number);
    }
}
```

#### Stream 방식
```java
List<Integer> evenNumbers = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
```

### 비교
| 방식 | 특징 |
|---|---|
| 기존 방식 | 반복문과 조건문을 직접 작성해야 하고, 필터링 로직이 분산된다 |
| Stream 방식 | 필터링 흐름이 한눈에 보이고, 가독성이 높아진다 |

---

## 5. 핵심 메서드 예시

| 메서드 | 설명 | 예시 |
|---|---|---|
| filter | 조건에 맞는 데이터만 남김 | `filter(n -> n % 2 == 0)` |
| map | 데이터를 변환 | `map(n -> n * 2)` |
| sorted | 정렬 | `sorted()` |
| collect | 결과를 모아 리스트 등으로 반환 | `collect(Collectors.toList())` |
| forEach | 각 데이터를 순회하며 작업 수행 | `forEach(System.out::println)` |
| reduce | 데이터들을 하나로 합침 | `reduce(0, Integer::sum)` |

---

## 6. 실제로 왜 네이버 뉴스 필터링에 stream을 쓰는가?

```java
List<NewsItem> filtered = newsItems.stream()
    .filter(this::isPressAllowed)
    .collect(Collectors.toList());
```

이 흐름은 아래와 같다.

- `newsItems.stream()`: 뉴스 리스트의 스트림 생성
- `filter(this::isPressAllowed)`: 허용된 언론사의 뉴스만 남긴다
- `collect(Collectors.toList())`: 필터링된 뉴스들을 리스트로 다시 모은다

### 기존 방식으로 작성하면
```java
List<NewsItem> filtered = new ArrayList<>();
for (NewsItem item : newsItems) {
    if (isPressAllowed(item)) {
        filtered.add(item);
    }
}
```

### 비교
| 방식 | 장점 |
|---|---|
| stream 방식 | 필터링 흐름이 명확하고, 필터 로직을 한눈에 볼 수 있다 |
| for문 방식 | 익숙하지만, 로직과 데이터 처리 흐름이 분리되지 않는다 |
```
*/
