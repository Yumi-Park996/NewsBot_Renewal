package service;

import model.NewsItem;

import java.util.List;
import java.util.stream.Collectors;

public class NewsFilter {

    // 허용 언론사 리스트 확장 (총 8개로 확대)
    private static final List<String> DEFAULT_ALLOWED_PRESS = List.of(
            "매일경제", "한국경제", "헤럴드경제",
            "이데일리", "파이낸셜뉴스", "연합뉴스", "뉴스1", "뉴시스"
    );

    public List<NewsItem> filterByPress(List<NewsItem> newsItems) {
        System.out.println("필터링 전 기사 수: " + newsItems.size());
        for (NewsItem item : newsItems) {
            System.out.println("기사 제목: " + item.getTitle());
            System.out.println("기사 링크: " + item.getoriginallink());
            System.out.println("언론사 매칭 결과: " +
                    DEFAULT_ALLOWED_PRESS.stream().anyMatch(press ->
                            item.getoriginallink().contains(getPressDomain(press))
                    )
            );
        }
        List<NewsItem> filtered = newsItems.stream()
                .filter(this::isPressAllowed)
                .collect(Collectors.toList());
        System.out.println("필터링 후 기사 수: " + filtered.size());
        return filtered;
    }

    private boolean isPressAllowed(NewsItem item) {
        boolean isAllowed = DEFAULT_ALLOWED_PRESS.stream().anyMatch(press ->
                item.getoriginallink().contains(getPressDomain(press))
        );
        System.out.println("기사 제목: " + item.getTitle());
        System.out.println("기사 링크: " + item.getoriginallink());
        System.out.println("필터링 결과: " + isAllowed);
        return isAllowed;
    }

    private String getPressDomain(String pressName) {
        return switch (pressName) {
            case "매일경제" -> "mk.co.kr";
            case "한국경제" -> "hankyung.com";
            case "헤럴드경제" -> "heraldcorp.com";
            case "이데일리" -> "edaily.co.kr";
            case "파이낸셜뉴스" -> "fnnews.com";
            case "연합뉴스" -> "yna.co.kr";
            case "뉴스1" -> "news1.kr";
            case "뉴시스" -> "newsis.com";
            default -> "";
        };
    }
}
