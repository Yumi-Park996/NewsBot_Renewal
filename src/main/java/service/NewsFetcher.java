package service;

import com.google.gson.*;
import model.NewsItem;
import model.SortType;
import util.HttpClientHelper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NewsFetcher {
    private final Logger logger;

    public NewsFetcher() {
        logger = Logger.getLogger(NewsFetcher.class.getName());
        logger.info("✅ Monitoring_news 객체 생성");
    }

    /*
     * 네이버 뉴스 API에서 뉴스 데이터를 가져옴
     * @param keyword 검색 키워드
     * @param display 검색 결과 수 (최대 100)
     * @param start 시작 위치 (페이징)
     * @param sort 정렬 방식 (sim, date)
     * @return 뉴스 아이템 리스트 (필터링 및 중복 제거 전 상태)
     */
    public List<NewsItem> fetchNews(String keyword, int display, int start, SortType sort) {
        logger.info("🔍 키워드: " + keyword);
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("🚨 환경 변수 'KEYWORD'가 설정되지 않았습니다.");
        }

        String url = "https://openapi.naver.com/v1/search/news.json";
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        String params = "query=%s&display=%d&start=%d&sort=%s".formatted(
                encodedKeyword, display, start, sort.value
        );

        String jsonResponse = HttpClientHelper.get(url + "?" + params);
        System.out.println(jsonResponse);
        if (jsonResponse == null) {
            logger.warning("❌ 네이버 뉴스 API 응답 없음");
            return new ArrayList<>();  // 응답 실패 시 빈 리스트 반환
        }

        List<NewsItem> newsItems = parseNews(jsonResponse);  // 여기가 빠져있을 가능성 있음
        System.out.println("뉴스 파싱 완료, 개수: " + newsItems.size());

        return newsItems;
    }

    /**
     * 네이버 뉴스 API 응답(JSON)을 파싱해 NewsItem 리스트로 변환
     * @param jsonResponse 네이버 뉴스 API 응답(JSON)
     * @return 뉴스 아이템 리스트
     */
    public static List<NewsItem> parseNews(String jsonResponse) {
        List<NewsItem> newsItems = new ArrayList<>();

        // JSON 파싱
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // "items" 키가 존재하는지 확인 후 가져오기
        JsonArray items = jsonObject.has("items") ? jsonObject.getAsJsonArray("items") : new JsonArray();
        System.out.println("파싱된 뉴스 개수: " + items.size());  // 디버깅 추가

        for (JsonElement element : items) {
            JsonObject obj = element.getAsJsonObject();

            // Null 방지 처리
            String title = obj.has("title") ? obj.get("title").getAsString().replaceAll("<.*?>", "") : "제목 없음";
            String originallink = obj.has("originallink") ? obj.get("originallink").getAsString() : "#";
            String link = obj.has("link") ? obj.get("link").getAsString() : "#";
            String description = obj.has("description") ? obj.get("description").getAsString().replaceAll("<.*?>", "") : "설명 없음";
            String pubDate = obj.has("pubDate") ? obj.get("pubDate").getAsString() : "날짜 없음";

            NewsItem newsItem = new NewsItem(title, originallink, link, description, pubDate);
            newsItems.add(newsItem);
        }

        System.out.println("최종 뉴스 개수: " + newsItems.size());  // 디버깅 추가
        return newsItems;
    }
}
