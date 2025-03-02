package app;  // 패키지 선언, 해당 클래스가 app 패키지에 속함

import model.NewsItem;  // 뉴스 데이터를 담는 모델 클래스 임포트
import model.SortType;  // 뉴스 정렬 타입 (정확도순, 최신순 등) 정의한 enum 임포트
import service.*;  // 뉴스 수집, 필터링, 중복 제거, LLM 처리, 포맷팅, 이메일 발송 서비스 임포트

import java.util.List;  // List 컬렉션 사용을 위한 임포트

public class AppNews {  // 메인 실행 클래스 정의

    private static final int INITIAL_SLEEP_TIME = 5000;  // 뉴스별 요청 간 대기 시간 5초 설정 (밀리초 단위)
    private static final int MAX_RETRIES = 5;  // LLM 호출 실패 시 최대 재시도 횟수 5회 설정

    public static void main(String[] args) {  // 프로그램 시작 지점, 메인 메서드 정의
        System.out.println("KEYWORD = " + "해외 주식");  // 수집할 뉴스 키워드 로그 출력

        // 1. 뉴스 수집 단계
        NewsFetcher fetcher = new NewsFetcher();  // 뉴스 수집 서비스 객체 생성
        List<NewsItem> allNews = fetcher.fetchNews("해외 주식", 100, 1, SortType.sim);  // 키워드로 뉴스 100건 수집 (정확도순)
        System.out.println("수집된 뉴스 개수: " + allNews.size());  // 수집된 뉴스 개수 출력

        // 2. 언론사 필터링 단계
        NewsFilter filter = new NewsFilter();  // 뉴스 필터링 서비스 객체 생성
        List<NewsItem> filteredNews = filter.filterByPress(allNews);  // 특정 언론사 제외 필터링 적용
        System.out.println("언론사 필터링 후 뉴스 개수: " + filteredNews.size());  // 필터링 후 개수 출력

        // 3. 중복 제거 단계
        NewsDeduplicator deduplicator = new NewsDeduplicator();  // 중복 제거 서비스 객체 생성
        List<NewsItem> deduplicatedNews = deduplicator.removeDuplicates(filteredNews);  // 유사 뉴스 제거
        System.out.println("중복 제거 후 뉴스 개수: " + deduplicatedNews.size());  // 중복 제거 후 개수 출력

        // 4. LLMProcessor 생성 단계
        LLMProcessor llmProcessor = new LLMProcessor();  // Gemini 기반 LLM 처리 객체 생성

        // 5. 뉴스별 요약 생성 단계
        for (NewsItem news : deduplicatedNews) {  // 중복 제거된 뉴스 목록 순회
            String summaryPrompt = llmProcessor.createArticleSummaryPrompt(news);  // 해당 뉴스 요약 요청 프롬프트 생성

            String summary = callGeminiWithRetry(llmProcessor, summaryPrompt);  // 프롬프트로 Gemini 호출 (재시도 로직 포함)
            news.setSummary(summary);  // 뉴스 객체에 요약 저장

            sleepWithLog(INITIAL_SLEEP_TIME);  // 다음 뉴스 요청 전 5초 대기
        }

        // 6. 투자 코멘트 생성 단계
        String investmentCommentPrompt = llmProcessor.createInvestmentCommentPrompt(deduplicatedNews);  // 오늘의 시장 요약 프롬프트 생성
        String investmentComment = callGeminiWithRetry(llmProcessor, investmentCommentPrompt);  // Gemini 호출 (재시도 로직 포함)

        sleepWithLog(INITIAL_SLEEP_TIME);  // 다음 요청 전 5초 대기

        // 7. 투자 꿀팁 생성 단계
        String investmentTipPrompt = llmProcessor.createInvestmentTipPrompt();  // 투자 조언 프롬프트 생성
        String investmentTip = callGeminiWithRetry(llmProcessor, investmentTipPrompt);  // Gemini 호출 (재시도 로직 포함)

        // 8. HTML 변환 단계
        String htmlContent = NewsFormatter.formatToHtml(deduplicatedNews, investmentComment, investmentTip);  // 뉴스 + 코멘트 + 꿀팁을 HTML로 변환

        // 생성된 투자 코멘트, 꿀팁, 최종 HTML 콘솔 출력
        System.out.println("오늘의 투자 코멘트: " + investmentComment);
        System.out.println("오늘의 투자 꿀팁: " + investmentTip);
        System.out.println("최종 HTML: " + htmlContent);

        // 9. 뉴스레터 이메일 발송 단계
        try {
            List<String> recipients = List.of(  // 수신자 이메일 주소 목록 정의
                    "byumm315@gmail.com",
                    "byumm315@naver.com",
                    "jungwon1998@naver.com",
                    "yuyumam3@naver.com",
                    "umipapa@daum.net"
            );

            // 뉴스레터 발송 (제목: 해외주식 데일리 브리핑 + 날짜)
            NewsletterSender.sendNewsletterDirect(
                    recipients,
                    "📬 해외주식 데일리 브리핑 - " + java.time.LocalDate.now(),
                    htmlContent
            );
        } catch (Exception e) {  // 발송 실패 시 예외 처리
            e.printStackTrace();  // 오류 스택 출력
            System.err.println("❌ 뉴스레터 발송 실패");  // 발송 실패 메시지 출력
        }
    }

    /**
     * Gemini 호출 시 재시도 및 백오프 로직 포함 메서드
     */
    private static String callGeminiWithRetry(LLMProcessor processor, String prompt) {
        int retryCount = 0;  // 현재 재시도 횟수
        int sleepTime = INITIAL_SLEEP_TIME;  // 재시도 시 대기 시간 (초기값 5초)

        while (retryCount < MAX_RETRIES) {  // 최대 재시도 횟수까지 반복
            String response = processor.callGemini(prompt);  // Gemini API 호출 (프롬프트 전달)

            // 정상 응답이면 바로 반환
            if (response != null && !response.contains("429") && !response.startsWith("오류: 429")) {
                return response;
            }

            // 오류 발생 시 로그 출력 및 대기
            System.out.println("❗ Gemini 요청 실패 (재시도 " + (retryCount + 1) + "/" + MAX_RETRIES + ")");
            sleepWithLog(sleepTime);

            // 재시도 대기 시간은 지수 증가 (5초 -> 10초 -> 20초 ...)
            sleepTime *= 2;
            retryCount++;
        }

        // 모든 재시도 실패 시 기본 응답 반환
        return "⚠️ 요약 실패 (모든 재시도 실패)";
    }

    /**
     * 지정된 시간만큼 대기하고 로그 출력하는 메서드
     */
    private static void sleepWithLog(int millis) {
        try {
            System.out.println("⏳ 요청 간 대기 (" + (millis / 1000) + "초)");  // 대기 시간 로그 출력
            Thread.sleep(millis);  // 실제 대기
        } catch (InterruptedException e) {  // 인터럽트 예외 발생 시
            Thread.currentThread().interrupt();  // 현재 스레드 인터럽트 상태 복원
        }
    }
}
