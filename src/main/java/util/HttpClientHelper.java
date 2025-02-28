package util;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;

public class HttpClientHelper {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String get(String url) {
        String clientId = ConfigLoader.NAVER_CLIENT_ID;
        String clientSecret = ConfigLoader.NAVER_CLIENT_SECRET;

        System.out.println("🛠️ [DEBUG] NAVER_CLIENT_ID = " + clientId);
        System.out.println("🛠️ [DEBUG] NAVER_CLIENT_SECRET = " + clientSecret);
        System.out.println("🛠️ [DEBUG] 요청 URL = " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("✅ 응답 상태 코드: " + response.statusCode());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("❌ API 요청 실패: " + response.body());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("❌ 오류 발생: " + e.getMessage());
            return null;
        }
    }
}
