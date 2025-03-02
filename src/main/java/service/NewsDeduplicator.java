package service;  // 이 클래스가 service 패키지에 속함을 나타냄

import model.NewsItem;  // 뉴스 아이템 데이터 모델 클래스 임포트
import org.apache.commons.text.similarity.JaroWinklerSimilarity;  // 문자열 유사도 계산 라이브러리 임포트

import java.util.ArrayList;  // 동적 배열 리스트 임포트
import java.util.List;  // List 인터페이스 임포트

public class NewsDeduplicator {  // 뉴스 중복 제거 기능을 담당하는 클래스 정의

    // JaroWinkler 알고리즘 객체 생성 (문자열 유사도 계산용)
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();

    // 두 뉴스 제목이 0.7 (70%) 이상 비슷하면 중복으로 간주하는 기준치 설정
    private final double SIMILARITY_THRESHOLD = 0.7;

    // 뉴스 리스트에서 중복 기사를 제거하는 메서드
    public List<NewsItem> removeDuplicates(List<NewsItem> newsItems) {
        List<NewsItem> deduplicated = new ArrayList<>();  // 중복 제거 후 결과 리스트

        for (NewsItem current : newsItems) {  // 입력 뉴스 리스트 반복
            if (isDuplicate(deduplicated, current)) {  // 이미 추가된 기사들과 비교해서 중복이면
                continue;  // 중복이니까 현재 뉴스는 추가 안 하고 건너뜀
            }
            deduplicated.add(current);  // 중복이 아니면 결과 리스트에 추가
        }

        return deduplicated;  // 중복 제거된 뉴스 리스트 반환
    }

    // 특정 뉴스가 이미 있는 뉴스들과 중복인지 확인하는 메서드
    private boolean isDuplicate(List<NewsItem> existingItems, NewsItem current) {
        for (NewsItem existing : existingItems) {  // 이미 추가된 뉴스 리스트 반복
            if (areTitlesSimilar(existing.getTitle(), current.getTitle())) {  // 제목 비교해서 유사하면
                return true;  // 중복이라고 판단
            }
        }
        return false;  // 하나도 유사한 게 없으면 중복 아님
    }

    // 두 뉴스 제목이 유사한지 판단하는 메서드
    private boolean areTitlesSimilar(String title1, String title2) {
        // 두 제목을 정규화 (HTML 태그 제거 + 특수문자 제거 + 소문자 변환)
        String normalized1 = normalizeTitle(title1);
        String normalized2 = normalizeTitle(title2);

        // Jaro-Winkler 알고리즘으로 유사도 계산
        double score = similarity.apply(normalized1, normalized2);

        // 유사도 점수가 기준치 이상이면 제목이 비슷하다고 판단
        return score >= SIMILARITY_THRESHOLD;
    }

    // 제목을 비교하기 쉽게 정규화하는 메서드
    private String normalizeTitle(String title) {
        return title.replaceAll("<[^>]*>", "")  // 제목에서 HTML 태그 제거
                .replaceAll("[^\\p{L}\\p{N}]", "")  // 한글, 영문, 숫자 제외하고 특수문자 제거
                .toLowerCase();  // 전부 소문자로 변환 (대소문자 구분 없애기)
    }
}
