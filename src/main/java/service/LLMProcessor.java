package service;  // 해당 클래스가 service 패키지에 속함을 나타냄

import util.ConfigLoader;  // 설정값 (API 키, URL)을 불러오기 위한 클래스 임포트
import model.NewsItem;  // 뉴스 데이터 모델 클래스 임포트
import org.apache.commons.text.StringEscapeUtils;  // HTML 엔티티 디코딩 유틸 임포트

import java.net.URI;  // HTTP 요청을 위한 URI 클래스 임포트
import java.net.http.HttpClient;  // HTTP 클라이언트 클래스 임포트
import java.net.http.HttpRequest;  // HTTP 요청 클래스 임포트
import java.net.http.HttpResponse;  // HTTP 응답 클래스 임포트
import java.nio.charset.StandardCharsets;  // 문자열 인코딩 지정 (UTF-8)
import java.util.List;  // 뉴스 목록 처리를 위한 List 임포트
import java.util.regex.Matcher;  // 정규식 매칭 도구 임포트
import java.util.regex.Pattern;  // 정규식 패턴 정의 도구 임포트

public class LLMProcessor {  // Gemini API 호출 및 프롬프트 생성 담당 클래스 정의

    // Gemini API 키 (ConfigLoader에서 불러옴)
    private static final String geminiApiKey = ConfigLoader.GEMINI_API_KEY;

    // Gemini API URL (ConfigLoader에서 불러옴)
    private static final String apiUrl = ConfigLoader.API_URL;

    // 문자열 정리 및 클린업 메서드
    private String cleanText(String input) {
        if (input == null) {  // 입력이 null이면 빈 문자열 반환
            return "";
        }

        // 1. HTML 엔티티 디코딩 (예: &amp; → &)
        String unescaped = StringEscapeUtils.unescapeHtml4(input);

        // 2. 앞뒤 공백 제거
        unescaped = unescaped.trim();

        // 3. 양끝이 큰따옴표로 감싸져 있으면 제거
        if (unescaped.startsWith("\"") && unescaped.endsWith("\"")) {
            unescaped = unescaped.substring(1, unescaped.length() - 1).trim();
        } 
        // 4. 양끝이 작은따옴표로 감싸져 있으면 제거
        else if (unescaped.startsWith("'") && unescaped.endsWith("'")) {
            unescaped = unescaped.substring(1, unescaped.length() - 1).trim();
        }

        return unescaped;  // 정리된 문자열 반환
    }

    // 투자 코멘트용 프롬프트 생성 메서드
    public String createInvestmentCommentPrompt(List<NewsItem> newsList) {
        StringBuilder sb = new StringBuilder();  // 문자열 조합 객체 생성
        sb.append("너는 해외주식 투자 전문가야...");  // 프롬프트 기본 설명 추가

        sb.append("뉴스 목록:\n");
        for (NewsItem news : newsList) {  // 뉴스 목록 반복
            sb.append("- ").append(cleanText(news.getTitle()))  // 제목 추가 (클린업 후)
                    .append(": ").append(cleanText(news.getDescription()))  // 설명 추가 (클린업 후)
                    .append("\n");
        }

        // 작성 조건 추가
        sb.append("\n조건:\n");
        sb.append("- 해외주식 시장 흐름을 한눈에 알 수 있게 작성\n");
        sb.append("- 너무 부정적이거나 긍정적으로 치우치지 말고 균형감 있게 작성\n");
        sb.append("- 존댓말로 작성\n");
        sb.append("- 길이는 1~2문장으로\n\n");
        sb.append("오늘의 투자 코멘트:");  // 마지막 프롬프트 마무리

        return sb.toString();  // 최종 프롬프트 반환
    }

    // 기사 요약용 프롬프트 생성 메서드
    public String createArticleSummaryPrompt(NewsItem newsItem) {
        String title = cleanText(newsItem.getTitle());  // 제목 클린업
        String description = cleanText(newsItem.getDescription());  // 설명 클린업

        // 프롬프트 구성 (기사 제목과 설명 포함)
        return String.format(
                "너는 해외주식 투자 뉴스를 요약하는 전문가야...\n" +
                        "제목: %s\n" +
                        "설명: %s\n\n" +
                        "조건:\n" +
                        "- 너무 어렵거나 전문적인 용어는 피할 것\n" +
                        "- 해외주식 투자자 입장에서 필요한 정보 위주로 작성\n" +
                        "- 한국어로 작성\n" +
                        "- 존댓말로 작성\n" +
                        "- 1줄 요약\n\n" +
                        "기사 요약:",
                title, description  // 제목과 설명 삽입
        );
    }

    // 투자 꿀팁용 프롬프트 생성 메서드
    public String createInvestmentTipPrompt() {
        // 단일 프롬프트 문자열 반환
        return "너는 해외주식 투자 전문가야...\n" +
                "조건:\n" +
                "- 해외주식 초보자부터 경험자까지 모두 이해할 수 있는 쉬운 표현\n" +
                "- 최근 해외주식 트렌드나 경제 흐름을 반영한 팁\n" +
                "- 한국어로 작성\n" +
                "- 존댓말로 작성\n" +
                "- 길이는 1문장으로\n\n" +
                "투자 꿀팁:";
    }

    // Gemini API 호출 메서드
    public String callGemini(String prompt) {
        System.out.println("🔎 Gemini 요청 프롬프트:\n" + prompt);  // 프롬프트 출력 로그

        if (geminiApiKey == null) {  // API 키 없을 경우 에러 메시지 반환
            return "환경 변수 GEMINI_API_KEY가 설정되지 않았습니다.";
        }

        // 요청 본문 구성 (프롬프트 포함 JSON 생성)
        String requestBody = """
                {
                    "contents": [
                        {"parts": [{"text": "%s"}]}
                    ]
                }
                """.formatted(escapeJson(prompt));

        // HTTP 클라이언트 및 요청 객체 생성
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))  // 요청 URL 지정
                .header("Content-Type", "application/json")  // 헤더 설정
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))  // POST 요청 및 본문 설정
                .build();

        try {
            // 요청 전송 및 응답 수신
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ 응답 상태코드: " + response.statusCode());  // 상태코드 출력

            if (response.statusCode() == 200) {  // 성공 응답일 경우
                return extractTextFromGeminiResponse(response.body());  // 응답 본문에서 텍스트 추출 및 반환
            } else {  // 실패 응답일 경우
                System.out.println("❌ 응답 본문: " + response.body());  // 에러 응답 출력
                return "오류: " + response.statusCode();  // 상태코드 포함 오류 메시지 반환
            }
        } catch (Exception e) {  // 요청 중 예외 발생 시
            e.printStackTrace();  // 예외 내용 출력
            return "예외 발생: " + e.getMessage();  // 예외 메시지 반환
        }
    }

    // Gemini 응답에서 텍스트 추출하는 메서드
    private String extractTextFromGeminiResponse(String json) {
        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+?)\"");  // "text": "내용" 형태 추출 패턴
        Matcher matcher = pattern.matcher(json);  // 응답 본문과 패턴 매칭

        if (matcher.find()) {  // 매칭 성공 시
            return matcher.group(1).replace("\\n", "\n").strip();  // 개행 복원 및 앞뒤 공백 제거 후 반환
        }

        return "응답에서 텍스트 찾기 실패";  // 매칭 실패 시 기본 메시지 반환
    }

    // JSON 문자열 이스케이프 처리 메서드
    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")  // 역슬래시 이스케이프
                .replace("\"", "\\\"")  // 큰따옴표 이스케이프
                .replace("\n", "\\n");  // 개행 문자 이스케이프
    }
}
