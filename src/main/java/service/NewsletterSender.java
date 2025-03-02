package service;  // 해당 클래스가 service 패키지에 속함을 명시

import util.ConfigLoader;  // 환경 변수 및 설정값을 불러오는 유틸 클래스 임포트

import java.io.IOException;  // 입출력 예외 처리를 위한 IOException 임포트
import java.net.URI;  // HTTP 요청 대상 주소를 나타내는 URI 클래스 임포트
import java.net.http.HttpClient;  // HTTP 요청을 보내기 위한 HttpClient 클래스 임포트
import java.net.http.HttpRequest;  // HTTP 요청 정보를 담는 HttpRequest 클래스 임포트
import java.net.http.HttpResponse;  // HTTP 응답 정보를 담는 HttpResponse 클래스 임포트
import java.util.List;  // 리스트 자료구조 사용을 위한 List 임포트
import java.util.stream.Collectors;  // 스트림 결과를 리스트로 변환할 때 사용하는 Collectors 임포트

public class NewsletterSender {  // 뉴스레터 이메일을 발송하는 역할을 담당하는 클래스 정의

    // Brevo API 키 (환경 변수에서 불러옴)
    private static final String API_KEY = ConfigLoader.API_KEY;

    // Brevo에서 인증된 발신자 이메일 (환경 변수에서 불러옴)
    private static final String SENDER_EMAIL = ConfigLoader.SENDER_EMAIL;

    /**
     * 여러 수신자에게 뉴스레터를 발송하는 메서드 (직접 Brevo SMTP API 호출)
     * @param recipientEmails 수신자 이메일 주소 리스트
     * @param subject 이메일 제목
     * @param htmlContent 이메일 본문 (HTML 형식)
     */
    public static void sendNewsletterDirect(List<String> recipientEmails, String subject, String htmlContent) {
        // API 키가 설정되지 않았을 경우 예외 발생
        if (API_KEY == null || API_KEY.isEmpty()) {
            throw new IllegalStateException("BREVO_API_KEY 환경변수가 설정되지 않았습니다.");
        }

        try {
            // Brevo SMTP API 엔드포인트 URL
            String apiUrl = "https://api.brevo.com/v3/smtp/email";

            // 수신자 이메일 리스트를 JSON 배열 형태의 문자열로 변환
            // 예: [{"email":"user1@example.com"},{"email":"user2@example.com"}]
            String toJsonArray = recipientEmails.stream()
                    .map(email -> String.format("{\"email\":\"%s\"}", email))  // 각 이메일을 {"email":"..."} 형식으로 변환
                    .collect(Collectors.joining(","));  // 쉼표로 이어붙여 하나의 JSON 배열 문자열로 생성

            // Brevo API 요청 본문 (JSON 문자열) 구성
            // - 발신자 정보 (name, email)
            // - 수신자 목록 (to)
            // - 이메일 제목 (subject)
            // - 이메일 본문 (htmlContent)
            String requestBody = """
                {
                    "sender": {"name": "해외주식 데일리 브리핑", "email": "%s"},
                    "to": [%s],
                    "subject": "%s",
                    "htmlContent": "%s"
                }
            """.formatted(
                    SENDER_EMAIL,               // 발신자 이메일 주소 (환경변수에서 불러온 값)
                    toJsonArray,                 // 수신자 이메일 리스트 (JSON 배열 형태)
                    escapeJson(subject),         // 이메일 제목 (특수문자 이스케이프 처리)
                    escapeJson(htmlContent)      // 이메일 본문 (특수문자 이스케이프 처리)
            );

            // HTTP 클라이언트 생성 (HTTP 요청 전송 담당 객체)
            HttpClient client = HttpClient.newHttpClient();

            // HTTP POST 요청 객체 생성
            // - 요청 URL: apiUrl (Brevo API 엔드포인트)
            // - 요청 헤더: accept (application/json 응답 기대)
            // - 요청 헤더: api-key (Brevo API 인증 키)
            // - 요청 헤더: Content-Type (요청 본문이 JSON 형식임을 명시)
            // - 요청 본문: 위에서 구성한 JSON 문자열 (requestBody)
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))  // 요청 URL 지정
                    .header("accept", "application/json")  // 응답은 JSON 형태로 받겠다고 지정
                    .header("api-key", API_KEY)  // 인증용 API 키 설정
                    .header("Content-Type", "application/json")  // 요청 본문은 JSON 형식임을 명시
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))  // POST 요청 + 본문 설정
                    .build();

            // HTTP 요청 전송 및 응답 수신
            // - 응답 본문은 문자열로 받음
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 본문 출력 (성공/실패 여부와 관계없이 서버 응답 로그 출력)
            System.out.println("📧 Brevo Response: " + response.body());

            // 상태 코드가 201이 아니면 (성공이 아니면) 에러 로그 출력
            if (response.statusCode() != 201) {
                System.err.println("❌ 뉴스레터 전송 실패 - 상태 코드: " + response.statusCode());  // 상태 코드 출력
                System.err.println("❌ 응답 내용: " + response.body());  // 응답 본문 출력
            } else {
                // 성공 시 수신자 리스트와 함께 성공 로그 출력
                System.out.println("✅ 뉴스레터 전송 성공: " + recipientEmails);
            }

        } catch (IOException | InterruptedException e) {  // 네트워크 오류, 스레드 인터럽트 예외 처리
            System.err.println("❌ 뉴스레터 전송 중 예외 발생");  // 예외 발생 로그 출력
            e.printStackTrace();  // 예외 상세 내용 출력 (디버깅용)
            Thread.currentThread().interrupt();  // 인터럽트 상태 복원 (InterruptedException 대응)
        }
    }

    /**
     * JSON 특수문자 이스케이프 처리 메서드
     * - 큰따옴표 (")를 JSON 표준에 맞게 \"로 변경
     */
    private static String escapeJson(String input) {
        return input.replace("\"", "\\\"");
    }
}
