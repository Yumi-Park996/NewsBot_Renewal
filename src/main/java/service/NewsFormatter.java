package service;

import model.NewsItem;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

public class NewsFormatter {

    public static String formatToHtml(List<NewsItem> newsList, String investmentComment, String investmentTip) {
        StringBuilder sb = new StringBuilder();

        sb.append("<!DOCTYPE html>");
        sb.append("<html lang='ko'>");
        sb.append("<head>");
        sb.append("<meta charset='UTF-8'>");
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1'>");  // 모바일 대응
        sb.append("<title>해외주식 데일리 브리핑</title>");
        sb.append("<style>");
        sb.append("body { margin: 0; padding: 15px; font-family: Arial, sans-serif; line-height: 1.7; font-size: 14px; }");  // 기본 폰트 축소, 여백 확보
        sb.append("table { width: 100%; border-collapse: collapse; }");
        sb.append(".header { background-color: #007bff; color: white; padding: 20px; font-size: 20px; font-weight: bold; text-align: center; margin-bottom: 20px; }");

        // 섹션 타이틀 - 모바일에서도 잘 보이게
        sb.append(".section-title { padding: 12px; font-weight: bold; font-size: 16px; margin-top: 20px; margin-bottom: 10px; }");
        sb.append(".section-title.comment { background-color: #eaf6ff; color: #007bff; }");
        sb.append(".section-title.news { background-color: #f9f0ff; color: #6f42c1; }");
        sb.append(".section-title.tip { background-color: #fff3cd; color: #856404; }");

        // 뉴스 아이템 - 모바일에서는 패딩, 마진 줄이기
        sb.append(".news-item { border-bottom: 1px solid #ddd; padding: 10px 0; margin-bottom: 12px; }");

        // 링크 및 텍스트
        sb.append("a { color: #007bff; text-decoration: none; font-weight: bold; }");
        sb.append("a:hover { text-decoration: underline; }");
        sb.append("p { margin: 6px 0; font-size: 14px; }");

        // 뉴스 번호
        sb.append(".news-number { font-weight: bold; font-size: 15px; color: #555; margin-bottom: 5px; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<table>");

        // 헤더
        sb.append("<tr><td class='header'>📬 해외주식 데일리 브리핑</td></tr>");

        // 투자 코멘트 섹션
        sb.append("<tr><td class='section-title comment'>✅ 오늘의 투자 코멘트</td></tr>");
        sb.append("<tr><td style='padding: 12px; margin-bottom: 15px; font-size: 15px; line-height: 1.7;'>")
                .append(escapeHtml(investmentComment))
                .append("</td></tr>");

        // 뉴스 요약 섹션
        sb.append("<tr><td class='section-title news'>📊 주요 뉴스 요약</td></tr>");

        for (NewsItem news : newsList) {
            sb.append("<tr><td class='news-item' style='padding: 12px; margin-bottom: 15px; font-size: 15px; line-height: 1.7;'>");
            sb.append("<strong><a href='").append(escapeHtml(news.getLink())).append("' target='_blank'>")
                    .append(escapeHtml(news.getTitle()))
                    .append("</a></strong>");
            sb.append("<p>").append(escapeHtml(news.getDescription())).append("</p>");
            sb.append("<p><strong>요약:</strong> ")
                    .append(escapeHtml(news.getSummary() == null ? "요약 없음" : news.getSummary()))
                    .append("</p>");
            sb.append("<p><em>발행일: ").append(escapeHtml(news.getPubDate())).append("</em></p>");
            sb.append("</td></tr>");
        }

        // 투자 꿀팁 섹션
        sb.append("<tr><td class='section-title tip' style='padding: 12px; margin-bottom: 15px; font-size: 15px; line-height: 1.7;'>💡 오늘의 투자 꿀팁</td></tr>");
        sb.append("<tr><td style='padding: 12px; margin-top: 15px; font-size: 15px; font-weight: bold;'>")
                .append(escapeHtml(investmentTip))
                .append("</td></tr>");

        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    // HTML Escape 유틸
    private static String escapeHtml(String input) {
        return input == null ? "" : StringEscapeUtils.escapeHtml4(input);
    }
}
