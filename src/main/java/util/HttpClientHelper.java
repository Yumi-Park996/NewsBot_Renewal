package util;  // 해당 클래스가 util 패키지에 속해 있음을 명시

import java.io.IOException;  // 입출력 예외 처리를 위한 라이브러리 임포트
import java.net.URI;  // URL을 URI 객체로 변환할 때 사용하는 클래스 임포트
import java.net.http.*;  // Java 11에서 추가된 HTTP 클라이언트 관련 클래스 전체 임포트

public class HttpClientHelper {  // HTTP 요청을 돕는 유틸성 클래스 정의

    // HttpClient 객체를 애플리케이션 전체에서 공유할 수 있도록 static final로 선언
    // 프로그램 시작 시 생성해두고, 계속 재사용하는 구조
    private static final HttpClient client = HttpClient.newHttpClient();

    // GET 요청을 보내고, 응답 본문(String)을 반환하는 메서드 정의
    public static String get(String url) {
        // 환경 변수에서 네이버 API 클라이언트 ID와 시크릿 키를 읽어옴
        String clientId = ConfigLoader.NAVER_CLIENT_ID;  // 네이버 클라이언트 ID (API 인증용)
        String clientSecret = ConfigLoader.NAVER_CLIENT_SECRET;  // 네이버 클라이언트 시크릿 (API 인증용)

        // 디버깅용으로 현재 사용하는 네이버 클라이언트 ID, 시크릿, 요청 URL 출력
        System.out.println("🛠️ [DEBUG] NAVER_CLIENT_ID = " + clientId);
        System.out.println("🛠️ [DEBUG] NAVER_CLIENT_SECRET = " + clientSecret);
        System.out.println("🛠️ [DEBUG] 요청 URL = " + url);

        // GET 요청 객체 생성
        // - 요청 대상 URI 설정
        // - HTTP 메서드 GET 지정
        // - 인증 정보를 요청 헤더에 추가 (네이버 오픈 API는 클라이언트 ID/시크릿을 헤더로 요구함)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))  // 요청 대상 URL을 URI 객체로 변환해 지정
                .GET()  // HTTP GET 방식으로 요청 설정
                .header("X-Naver-Client-Id", clientId)  // 네이버 API 클라이언트 ID 헤더 추가
                .header("X-Naver-Client-Secret", clientSecret)  // 네이버 API 클라이언트 시크릿 헤더 추가
                .build();  // 요청 객체 최종 생성

        try {
            // 클라이언트를 통해 HTTP 요청 전송 및 응답 수신
            // - 요청 객체(request)를 전달
            // - 응답 본문을 문자열(String)로 받도록 설정
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 상태 코드 출력 (성공/실패 여부 확인용)
            System.out.println("✅ 응답 상태 코드: " + response.statusCode());

            // 상태 코드가 200일 경우, 정상 응답으로 간주하고 응답 본문 반환
            if (response.statusCode() == 200) {
                return response.body();  // 정상 응답 본문 반환 (JSON 문자열 등)
            } else {
                // 상태 코드가 200이 아닐 경우, 에러 로그 출력 후 null 반환
                System.err.println("❌ API 요청 실패: " + response.body());  // 에러 내용 출력
                return null;  // 실패 시 null 반환 (호출하는 쪽에서 실패 처리 가능)
            }
        } catch (IOException | InterruptedException e) {  // 네트워크 오류 또는 스레드 인터럽트 발생 시
            System.err.println("❌ 오류 발생: " + e.getMessage());  // 예외 메시지 출력
            return null;  // 예외 발생 시 null 반환
        }
    }
}

