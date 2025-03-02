package model;

public enum SortType {
    sim("sim"), date("date");

    public final String value;

    SortType(String value) {
        this.value = value;
    }
}

/*
### enum이란?
enum은 열거형 (enumeration) 의 줄임말이에요.
서로 연관된 '상수 값들'을 한 곳에 모아서 관리할 때 쓰는 특수한 자료형입니다.

### 왜 쓰냐면?
서로 관련된 값들을 한 눈에 보이게 정리할 수 있음
오타 방지: 정해진 값만 쓸 수 있어서, 실수로 엉뚱한 값 입력하는 걸 막아줌
가독성 향상: 의미 있는 이름으로 값들을 정리할 수 있어서 코드 읽기 쉬움

### 예시로 설명
```java
public enum SortType {
    DATE,   // 날짜순
    RELEVANCE  // 연관도순
}
```
SortType이라는 열거형을 만들고, 안에 DATE와 RELEVANCE라는 두 가지 값만 넣었어요.
이제 이 SortType은 날짜순 정렬과 연관도순 정렬이라는 두 값만 가질 수 있는 타입이 된 거예요.
즉, SortType에는 DATE 아니면 RELEVANCE만 들어갈 수 있어서 실수할 일이 줄어요.
*/
