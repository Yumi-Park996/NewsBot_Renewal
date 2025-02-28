package service;
import util.ConfigLoader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class NewsletterSender {

    private static final String API_KEY = ConfigLoader.API_KEY;
    private static final String SENDER_EMAIL = ConfigLoader.SENDER_EMAIL;  // Brevo 인증 발신자

    /**
     * 여러 수신자에게 뉴스레터 발송
     */
    public static void sendNewsletterDirect(List<String> recipientEmails, String subject, String htmlContent) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("BREVO_API_KEY 환경변수가 설정되지 않았습니다.");
        }

        try {
            String apiUrl = "https://api.brevo.com/v3/smtp/email";

            // 수신자 배열 JSON 생성
            String toJsonArray = recipientEmails.stream()
                    .map(email -> String.format("{\"email\":\"%s\"}", email))
                    .collect(Collectors.joining(","));

            String requestBody = """
                {
                    "sender": {"name": "해외주식 데일리 브리핑", "email": "%s"},
                    "to": [%s],
                    "subject": "%s",
                    "htmlContent": "%s"
                }
            """.formatted(
                    SENDER_EMAIL,
                    toJsonArray,
                    escapeJson(subject),
                    escapeJson(htmlContent)
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("accept", "application/json")
                    .header("api-key", API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("📧 Brevo Response: " + response.body());

            if (response.statusCode() != 201) {
                System.err.println("❌ 뉴스레터 전송 실패 - 상태 코드: " + response.statusCode());
                System.err.println("❌ 응답 내용: " + response.body());
            } else {
                System.out.println("✅ 뉴스레터 전송 성공: " + recipientEmails);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ 뉴스레터 전송 중 예외 발생");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private static String escapeJson(String input) {
        return input.replace("\"", "\\\"");
    }
}
