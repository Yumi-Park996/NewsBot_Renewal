package service;  // 해당 클래스가 service 패키지에 속함을 나타냄

import com.google.gson.*;  // JSON 파싱을 위한 Gson 라이브러리 임포트
import model.NewsItem;  // 뉴스 데이터 모델 클래스 임포트
import model.SortType;  // 정렬 타입 (sim: 정확도순, date: 날짜순) enum 임포트
import util.HttpClientHelper;  // HTTP 요청 처리를 위한 헬퍼 클래스 임포트

import java.net.URLEncoder;  // 검색어를 URL에 맞게 인코딩하기 위한 클래스 임포트
import java.nio.charset.StandardCharsets;  // UTF-8 인코딩 설정을 위한 임포트
import java.util.ArrayList;  // 동적 배열 리스트 임포트
import java.util.List;  // List 인터페이스 임포트
import java.util.logging.Logger;  // 로깅을 위한 Logger 클래스 임포트

public class NewsFetcher {  // 네이버 뉴스 API에서 뉴스를 가져오는 역할을 하는 클래스 정의

    private final Logger logger;  // 클래스 내에서 사용할 로거 객체 선언

    // 생성자 (클래스 객체 생성 시 로거 초기화 및 로그 출력)
    public NewsFetcher() {
        logger = Logger.getLogger(NewsFetcher.class.getName());  // 로거 초기화
        logger.info("✅ Monitoring_news 객체 생성");  // 객체 생성 로그 출력
    }

    /*
     * 네이버 뉴스 API에서 뉴스 데이터를 가져오는 메서드
     * @param keyword 검색할 키워드
     * @param display 한 번에 가져올 뉴스 개수 (최대 100개)
     * @param start 검색 시작 위치 (페이징 처리용)
     * @param sort 정렬 방식 (정확도순: sim, 날짜순: date)
     * @return 뉴스 아이템 리스트 (필터링 및 중복 제거 전 원본 상태)
     */
    public List<NewsItem> fetchNews(String keyword, int display, int start, SortType sort) {
        logger.info("🔍 키워드: " + keyword);  // 검색 키워드 로그 출력

        // 키워드가 없으면 예외 발생 (필수 조건 체크)
        if (keyword == null || keyword.isEmpty()) {
            throw new IllegalArgumentException("🚨 환경 변수 'KEYWORD'가 설정되지 않았습니다.");
        }

        // 네이버 뉴스 API 기본 URL
        String url = "https://openapi.naver.com/v1/search/news.json";

        // 검색 키워드를 UTF-8로 URL 인코딩 (특수문자, 공백 처리)
        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);

        // 요청 파라미터 구성 (키워드, 표시 개수, 시작 위치, 정렬 방식 포함)
        String params = "query=%s&display=%d&start=%d&sort=%s".formatted(
                encodedKeyword, display, start, sort.value  // 정렬 타입의 value 사용 (sim, date)
        );

        // HTTP GET 요청으로 네이버 뉴스 API 호출, 응답 JSON 문자열로 받기
        String jsonResponse = HttpClientHelper.get(url + "?" + params);

        // 응답 본문 출력 (디버깅용)
        System.out.println(jsonResponse);

        // 응답이 없으면 경고 로그 출력 후 빈 리스트 반환
        if (jsonResponse == null) {
            logger.warning("❌ 네이버 뉴스 API 응답 없음");
            return new ArrayList<>();
        }

        // 응답 JSON을 파싱해 NewsItem 리스트로 변환
        List<NewsItem> newsItems = parseNews(jsonResponse);

        // 파싱 완료 후 뉴스 개수 출력
        System.out.println("뉴스 파싱 완료, 개수: " + newsItems.size());

        // 파싱한 뉴스 리스트 반환
        return newsItems;
    }

    /**
     * 네이버 뉴스 API 응답(JSON)을 파싱하여 NewsItem 리스트로 변환하는 메서드
     * @param jsonResponse 네이버 뉴스 API에서 받은 JSON 응답 문자열
     * @return NewsItem 객체 리스트
     */
    public static List<NewsItem> parseNews(String jsonResponse) {
        List<NewsItem> newsItems = new ArrayList<>();  // 결과 저장 리스트 생성

        // JSON 응답 문자열을 JsonObject로 파싱
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

        // "items" 키가 존재하는 경우, 해당 배열을 가져오고 없으면 빈 배열 반환
        JsonArray items = jsonObject.has("items") ? jsonObject.getAsJsonArray("items") : new JsonArray();

        // 파싱된 뉴스 개수 출력 (디버깅용)
        System.out.println("파싱된 뉴스 개수: " + items.size());

        // "items" 배열에 있는 각 뉴스 항목을 순회하며 NewsItem으로 변환
        for (JsonElement element : items) {
            JsonObject obj = element.getAsJsonObject();

            // 각 항목에서 필요한 정보 추출 (null 체크 및 기본값 설정 포함)
            // 제목 (HTML 태그 제거)
            String title = obj.has("title") ? obj.get("title").getAsString().replaceAll("<.*?>", "") : "제목 없음";
            // 원본 링크
            String originallink = obj.has("originallink") ? obj.get("originallink").getAsString() : "#";
            // 네이버 제공 링크
            String link = obj.has("link") ? obj.get("link").getAsString() : "#";
            // 설명 (HTML 태그 제거)
            String description = obj.has("description") ? obj.get("description").getAsString().replaceAll("<.*?>", "") : "설명 없음";
            // 기사 작성 날짜
            String pubDate = obj.has("pubDate") ? obj.get("pubDate").getAsString() : "날짜 없음";

            // NewsItem 객체 생성 (파싱한 데이터로 초기화)
            NewsItem newsItem = new NewsItem(title, originallink, link, description, pubDate);

            // 결과 리스트에 추가
            newsItems.add(newsItem);
        }

        // 최종 뉴스 개수 출력 (디버깅용)
        System.out.println("최종 뉴스 개수: " + newsItems.size());

        // 파싱 완료된 뉴스 리스트 반환
        return newsItems;
    }
}
