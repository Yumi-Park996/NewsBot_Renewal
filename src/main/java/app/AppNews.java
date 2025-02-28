package app;

import model.NewsItem;
import model.SortType;
import service.*;

import java.util.List;

public class AppNews {

    private static final int INITIAL_SLEEP_TIME = 5000;  // 첫 요청 간 대기 시간 (5초)
    private static final int MAX_RETRIES = 5;            // 최대 재시도 횟수

    public static void main(String[] args) {
        System.out.println("KEYWORD = " + "해외 주식");

        // 1. 뉴스 수집
        NewsFetcher fetcher = new NewsFetcher();
        List<NewsItem> allNews = fetcher.fetchNews("해외 주식", 100, 1, SortType.sim);
        System.out.println("수집된 뉴스 개수: " + allNews.size());

        // 2. 언론사 필터링
        NewsFilter filter = new NewsFilter();
        List<NewsItem> filteredNews = filter.filterByPress(allNews);
        System.out.println("언론사 필터링 후 뉴스 개수: " + filteredNews.size());

        // 3. 중복 제거
        NewsDeduplicator deduplicator = new NewsDeduplicator();
        List<NewsItem> deduplicatedNews = deduplicator.removeDuplicates(filteredNews);
        System.out.println("중복 제거 후 뉴스 개수: " + deduplicatedNews.size());

        // 4. LLMProcessor 생성 (이제 Gemini 전용)
        LLMProcessor llmProcessor = new LLMProcessor();

        // 5. 뉴스별 요약 처리 (각 뉴스별 요청 시 대기 + 재시도 처리 추가)
        for (NewsItem news : deduplicatedNews) {
            String summaryPrompt = llmProcessor.createArticleSummaryPrompt(news);

            String summary = callGeminiWithRetry(llmProcessor, summaryPrompt);
            news.setSummary(summary);

            sleepWithLog(INITIAL_SLEEP_TIME);  // 뉴스별 요청 간격 추가
        }

        // 6. 투자 코멘트 생성 (오늘의 시장 요약) - 대기 + 재시도 추가
        String investmentCommentPrompt = llmProcessor.createInvestmentCommentPrompt(deduplicatedNews);
        String investmentComment = callGeminiWithRetry(llmProcessor, investmentCommentPrompt);

        sleepWithLog(INITIAL_SLEEP_TIME);  // 요청 간 대기

        // 7. 투자 꿀팁 생성 (투자 조언) - 대기 + 재시도 추가
        String investmentTipPrompt = llmProcessor.createInvestmentTipPrompt();
        String investmentTip = callGeminiWithRetry(llmProcessor, investmentTipPrompt);

        // 8. 뉴스 목록을 HTML로 변환 (요약, 투자 코멘트, 투자 꿀팁 포함)
        String htmlContent = NewsFormatter.formatToHtml(deduplicatedNews, investmentComment, investmentTip);

        System.out.println("오늘의 투자 코멘트: " + investmentComment);
        System.out.println("오늘의 투자 꿀팁: " + investmentTip);
        System.out.println("최종 HTML: " + htmlContent);

        // 9. 뉴스레터 이메일 발송
        try {
            List<String> recipients = List.of(
                    "byumm315@gmail.com",
                    "byumm315@naver.com"
            );

            NewsletterSender.sendNewsletterDirect(
                    recipients,
                    "📬 해외주식 데일리 브리핑 - " + java.time.LocalDate.now(),
                    htmlContent
            );
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ 뉴스레터 발송 실패");
        }

    }

    /**
     * Gemini 호출에 재시도 및 백오프 로직 추가
     */
    private static String callGeminiWithRetry(LLMProcessor processor, String prompt) {
        int retryCount = 0;
        int sleepTime = INITIAL_SLEEP_TIME;  // 첫 재시도 대기 시간

        while (retryCount < MAX_RETRIES) {
            String response = processor.callGemini(prompt);  // 기존 callLLM에서 callGemini로 변경

            if (response != null && !response.contains("429") && !response.startsWith("오류: 429")) {
                return response;  // 정상 응답
            }

            System.out.println("❗ Gemini 요청 실패 (재시도 " + (retryCount + 1) + "/" + MAX_RETRIES + ")");
            sleepWithLog(sleepTime);

            // Exponential Backoff
            sleepTime *= 2;
            retryCount++;
        }

        return "⚠️ 요약 실패 (모든 재시도 실패)";
    }

    /**
     * 지정 시간만큼 대기 + 로그 출력
     */
    private static void sleepWithLog(int millis) {
        try {
            System.out.println("⏳ 요청 간 대기 (" + (millis / 1000) + "초)");
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
