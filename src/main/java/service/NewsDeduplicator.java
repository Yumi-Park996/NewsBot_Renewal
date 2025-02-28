package service;

import model.NewsItem;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;

import java.util.ArrayList;
import java.util.List;

public class NewsDeduplicator {

    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
    private final double SIMILARITY_THRESHOLD = 0.7;  // 85% 이상 비슷하면 같은 제목으로 간주

    public List<NewsItem> removeDuplicates(List<NewsItem> newsItems) {
        List<NewsItem> deduplicated = new ArrayList<>();

        for (NewsItem current : newsItems) {
            if (isDuplicate(deduplicated, current)) {
                continue;  // 유사한 제목이 이미 있으면 스킵
            }
            deduplicated.add(current);
        }

        return deduplicated;
    }

    private boolean isDuplicate(List<NewsItem> existingItems, NewsItem current) {
        for (NewsItem existing : existingItems) {
            if (areTitlesSimilar(existing.getTitle(), current.getTitle())) {
                return true;
            }
        }
        return false;
    }

    private boolean areTitlesSimilar(String title1, String title2) {
        String normalized1 = normalizeTitle(title1);
        String normalized2 = normalizeTitle(title2);

        double score = similarity.apply(normalized1, normalized2);
        return score >= SIMILARITY_THRESHOLD;  // 일정 유사도 넘으면 중복으로 간주
    }

    private String normalizeTitle(String title) {
        return title.replaceAll("<[^>]*>", "")  // HTML 태그 제거
                .replaceAll("[^\\p{L}\\p{N}]", "")  // 특수문자 제거 (한글, 영문, 숫자만 남김)
                .toLowerCase();
    }
}
