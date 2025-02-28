package service;

import util.ConfigLoader;
import model.NewsItem;
import org.apache.commons.text.StringEscapeUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LLMProcessor {

    private static final String geminiApiKey = ConfigLoader.GEMINI_API_KEY;
    private static final String apiUrl = ConfigLoader.API_URL;

    private String cleanText(String input) {
        if (input == null) {
            return "";
        }

        // 1. HTML 엔티티 복원
        String unescaped = StringEscapeUtils.unescapeHtml4(input);

        // 2. 앞뒤에 필요 없는 따옴표, 쌍따옴표, 공백 제거
        unescaped = unescaped.trim();

        if (unescaped.startsWith("\"") && unescaped.endsWith("\"")) {
            unescaped = unescaped.substring(1, unescaped.length() - 1).trim();
        } else if (unescaped.startsWith("'") && unescaped.endsWith("'")) {
            unescaped = unescaped.substring(1, unescaped.length() - 1).trim();
        }

        return unescaped;
    }

    public String createInvestmentCommentPrompt(List<NewsItem> newsList) {
        StringBuilder sb = new StringBuilder();
        sb.append("너는 해외주식 투자 전문가야. 오늘의 해외주식 관련 주요 뉴스 목록과 간략한 설명을 참고해, 투자자들이 꼭 알아야 할 오늘의 핵심 코멘트를 한 문장으로 작성해줘.\n\n");
        sb.append("뉴스 목록:\n");
        for (NewsItem news : newsList) {
            sb.append("- ").append(cleanText(news.getTitle())).append(": ").append(cleanText(news.getDescription())).append("\n");
        }
        sb.append("\n조건:\n");
        sb.append("- 해외주식 시장 흐름을 한눈에 알 수 있게 작성\n");
        sb.append("- 너무 부정적이거나 긍정적으로 치우치지 말고 균형감 있게 작성\n");
        sb.append("- 존댓말로 작성\n");
        sb.append("- 길이는 1~2문장으로\n\n");
        sb.append("오늘의 투자 코멘트:");
        return sb.toString();
    }

    public String createArticleSummaryPrompt(NewsItem newsItem) {
        String title = cleanText(newsItem.getTitle());
        String description = cleanText(newsItem.getDescription());

        return String.format(
                "너는 해외주식 투자 뉴스를 요약하는 전문가야.\n" +
                        "아래는 해외주식 관련 기사 제목과 설명이야. 이 기사를 투자자들이 쉽게 이해할 수 있도록 핵심 내용을 1줄로 요약해줘.\n\n" +
                        "제목: %s\n" +
                        "설명: %s\n\n" +
                        "조건:\n" +
                        "- 너무 어렵거나 전문적인 용어는 피할 것\n" +
                        "- 해외주식 투자자 입장에서 필요한 정보 위주로 작성\n" +
                        "- 한국어로 작성\n" +  // 한국어로 응답 유도
                        "- 존댓말로 작성\n" +
                        "- 1줄 요약\n\n" +
                        "기사 요약:",
                title, description
        );
    }

    public String createInvestmentTipPrompt() {
        return "너는 해외주식 투자 전문가야. 해외주식 투자자들에게 도움이 되는 실용적인 투자 꿀팁을 한 문장으로 작성해줘.\n\n" +
                "조건:\n" +
                "- 해외주식 초보자부터 경험자까지 모두 이해할 수 있는 쉬운 표현\n" +
                "- 최근 해외주식 트렌드나 경제 흐름을 반영한 팁\n" +
                "- 한국어로 작성\n" +  // 한국어로 응답 유도
                "- 존댓말로 작성\n" +
                "- 길이는 1문장으로\n\n" +
                "투자 꿀팁:";
    }

    public String callGemini(String prompt) {
        System.out.println("🔎 Gemini 요청 프롬프트:\n" + prompt);

        if (geminiApiKey == null) {
            return "환경 변수 GEMINI_API_KEY가 설정되지 않았습니다.";
        }

        String requestBody = """
                {
                    "contents": [
                        {"parts": [{"text": "%s"}]}
                    ]
                }
                """.formatted(escapeJson(prompt));

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ 응답 상태코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                return extractTextFromGeminiResponse(response.body());
            } else {
                System.out.println("❌ 응답 본문: " + response.body());
                return "오류: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "예외 발생: " + e.getMessage();
        }
    }

    private String extractTextFromGeminiResponse(String json) {
        Pattern pattern = Pattern.compile("\"text\"\\s*:\\s*\"([^\"]+?)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).replace("\\n", "\n").strip();  // 개행 복원 및 앞뒤 공백 제거
        }
        return "응답에서 텍스트 찾기 실패";
    }

    private String escapeJson(String str) {
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
