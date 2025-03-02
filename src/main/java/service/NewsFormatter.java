package service;  // 해당 클래스가 service 패키지에 속함을 명시

import model.NewsItem;  // 뉴스 아이템 모델 클래스 임포트

import org.apache.commons.text.StringEscapeUtils;  // HTML 특수문자 이스케이프 처리를 위한 라이브러리 임포트

import java.util.List;  // List 컬렉션 클래스 임포트

public class NewsFormatter {  // 뉴스 데이터를 HTML 형식으로 변환하는 역할을 하는 클래스 정의

    // 뉴스 리스트, 투자 코멘트, 투자 꿀팁을 받아서 HTML 형식으로 변환하는 메서드
    public static String formatToHtml(List<NewsItem> newsList, String investmentComment, String investmentTip) {
        StringBuilder sb = new StringBuilder();  // HTML 내용을 담을 StringBuilder 생성

        sb.append("<!DOCTYPE html>");  // HTML5 문서 타입 선언
        sb.append("<html lang='ko'>");  // 한국어 문서 지정
        sb.append("<head>");
        sb.append("<meta charset='UTF-8'>");  // 문자 인코딩 설정
        sb.append("<meta name='viewport' content='width=device-width, initial-scale=1'>");  // 모바일 대응을 위한 뷰포트 설정
        sb.append("<title>해외주식 데일리 브리핑</title>");  // 페이지 제목 설정
        sb.append("<style>");  // 스타일 정의 시작

        // 본문 기본 스타일 설정 (폰트, 여백, 줄 간격 등)
        sb.append("body { margin: 0; padding: 15px; font-family: Arial, sans-serif; line-height: 1.7; font-size: 14px; }");
        // 테이블 기본 스타일 설정 (테이블 폭 100%, 셀 경계 없음)
        sb.append("table { width: 100%; border-collapse: collapse; }");
        // 헤더 스타일 (파란 배경, 흰색 글자, 중앙 정렬)
        sb.append(".header { background-color: #007bff; color: white; padding: 20px; font-size: 20px; font-weight: bold; text-align: center; margin-bottom: 20px; }");

        // 섹션 타이틀 스타일 설정 (각 섹션마다 구분되는 색상 적용)
        sb.append(".section-title { padding: 12px; font-weight: bold; font-size: 16px; margin-top: 20px; margin-bottom: 10px; }");
        sb.append(".section-title.comment { background-color: #eaf6ff; color: #007bff; }");  // 투자 코멘트 섹션 색상
        sb.append(".section-title.news { background-color: #f9f0ff; color: #6f42c1; }");  // 뉴스 섹션 색상
        sb.append(".section-title.tip { background-color: #fff3cd; color: #856404; }");  // 투자 꿀팁 섹션 색상

        // 뉴스 아이템 스타일 (각 뉴스 항목 구분선 및 여백 설정)
        sb.append(".news-item { border-bottom: 1px solid #ddd; padding: 10px 0; margin-bottom: 12px; }");

        // 링크 스타일 (기본 파란색, 굵게 표시)
        sb.append("a { color: #007bff; text-decoration: none; font-weight: bold; }");
        sb.append("a:hover { text-decoration: underline; }");  // 링크에 마우스를 올리면 밑줄 표시
        sb.append("p { margin: 6px 0; font-size: 14px; }");  // 단락 기본 스타일

        // 뉴스 번호 스타일 설정 (굵게, 글자 크기 약간 크게)
        sb.append(".news-number { font-weight: bold; font-size: 15px; color: #555; margin-bottom: 5px; }");
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<table>");

        // 헤더 섹션 (제목 표시 영역)
        sb.append("<tr><td class='header'>📬 해외주식 데일리 브리핑</td></tr>");

        // 투자 코멘트 섹션 제목
        sb.append("<tr><td class='section-title comment'>✅ 오늘의 투자 코멘트</td></tr>");
        // 투자 코멘트 내용 출력
        sb.append("<tr><td style='padding: 12px; margin-bottom: 15px; font-size: 15px; line-height: 1.7;'>")
                .append(escapeHtml(investmentComment))  // 특수문자 이스케이프 후 출력
                .append("</td></tr>");

        // 뉴스 요약 섹션 제목
        sb.append("<tr><td class='section-title news'>📊 주요 뉴스 요약</td></tr>");

        // 각 뉴스 아이템 순회하며 HTML로 출력
        for (NewsItem news : newsList) {
            sb.append("<tr><td class='news-item' style='padding: 12px; margin-bottom: 15px; font-size: 15px; line-height: 1.7;'>");

            // 뉴스 제목과 링크
            sb.append("<strong><a href='").append(escapeHtml(news.getLink())).append("' target='_blank'>")
                    .append(escapeHtml(news.getTitle()))
                    .append("</a></strong>");

            // 뉴스 설명
            sb.append("<p>").append(escapeHtml(news.getDescription())).append("</p>");

            // 요약 (없으면 '요약 없음' 출력)
            sb.append("<p><strong>요약:</strong> ")
                    .append(escapeHtml(news.getSummary() == null ? "요약 없음" : news.getSummary()))
                    .append("</p>");

            // 발행일 표시
            sb.append("<p><em>발행일: ").append(escapeHtml(news.getPubDate())).append("</em></p>");
            sb.append("</td></tr>");
        }

        // 투자 꿀팁 섹션 제목
        sb.append("<tr><td class='section-title tip' style='padding: 12px; margin-bottom: 15px; font-size: 15px; line-height: 1.7;'>💡 오늘의 투자 꿀팁</td></tr>");
        // 투자 꿀팁 내용
        sb.append("<tr><td style='padding: 12px; margin-top: 15px; font-size: 15px; font-weight: bold;'>")
                .append(escapeHtml(investmentTip))  // 특수문자 이스케이프 후 출력
                .append("</td></tr>");

        sb.append("</table>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();  // 완성된 HTML 문자열 반환
    }

    // 입력 문자열의 특수문자를 HTML 엔티티로 변환 (XSS 방지 및 안전한 표시)
    private static String escapeHtml(String input) {
        return input == null ? "" : StringEscapeUtils.escapeHtml4(input);
    }
}
