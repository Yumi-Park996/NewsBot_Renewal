package model;  // 해당 클래스가 model 패키지에 속함을 나타냄

public class NewsItem {  // 뉴스 기사를 나타내는 데이터 모델 클래스 정의

    // 뉴스 제목
    private String title;

    // 원본 링크 (기사의 실제 원문 URL)
    private String originallink;

    // 검색 결과에서 제공하는 링크 (중개 링크일 가능성 있음)
    private String link;

    // 뉴스 요약 설명 (본문 일부 발췌)
    private String description;

    // 기사 발행 날짜 및 시간
    private String pubDate;

    // Gemini로 생성한 기사 요약 (초기값은 null)
    private String summary;

    // 생성자: 필수 필드 5개를 받아서 NewsItem 객체 생성
    public NewsItem(String title, String originallink, String link, String description, String pubDate) {
        this.title = title;                // 제목 초기화
        this.originallink = originallink;  // 원본 링크 초기화
        this.link = link;                  // 제공 링크 초기화
        this.description = description;    // 요약 설명 초기화
        this.pubDate = pubDate;            // 발행 날짜 초기화
    }

    // 뉴스 제목 반환
    public String getTitle() { return title; }

    // 원본 링크 반환 (메서드 이름은 소문자지만 원본 필드는 originallink)
    public String getoriginallink() { return originallink; }

    // 제공 링크 반환
    public String getLink() { return link; }

    // 뉴스 설명 반환
    public String getDescription() { return description; }

    // 발행 날짜 반환
    public String getPubDate() { return pubDate; }

    // Gemini 요약 반환
    public String getSummary() { return summary; }

    // Gemini 요약 저장 (외부에서 생성한 요약을 저장할 때 사용)
    public void setSummary(String summary) { this.summary = summary; }
}


/* 객체 <-> 문자열 변환 이유
1. 네이버 뉴스 API는 자바 객체로 응답하지 않음
* 네이버 뉴스 API는 자바 객체가 아니라 "JSON 문자열"로 응답을 줘요.
* 즉, 네이버는 "자바스럽게" 주는 게 아니라, 표준 데이터 포맷인 JSON으로 주는 거예요.
* 자바에서는 JSON을 바로 List<NewsItem>처럼 쓸 수 없어요.
* 따라서, JSON 문자열을 자바에서 다룰 수 있는 형태로 변환하는 과정이 필수적입니다.
2. 자바는 JSON을 직접 다룰 수 없음 (파싱 필요)
* 자바는 JSON을 기본적으로 이해하지 못해요.
* 따라서 Gson, Jackson, JsonParser 같은 라이브러리를 통해 JSON 문자열을 자바 객체로 변환해야 해요.
* 지금 코드에서는 JsonParser와 NewsItem을 활용해서
* "JSON 문자열" → "JsonObject" → "NewsItem"으로 바꾸는 흐름을 타는 거죠.
3. 자바 객체로 바꾸면 다루기 편함
* 네이버 응답 JSON을 그대로 쓰면, 코드가 이런 식으로 길어져요:
```java
jsonObject.getAsJsonArray("items").get(0).getAsJsonObject().get("title").getAsString()
```
* 그런데 NewsItem 객체로 만들어두면, 이렇게 편하게 다룰 수 있어요:
```java
newsItem.getTitle()
```
* 결국, "JSON은 전달받을 때만 필요하고, 실제 작업할 때는 자바 객체로 다루는 게 훨씬 편하기 때문입니다.
*/
