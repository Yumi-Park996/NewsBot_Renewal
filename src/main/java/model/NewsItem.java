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
