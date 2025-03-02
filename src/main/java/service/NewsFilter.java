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
