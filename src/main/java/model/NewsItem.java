package model;

public class NewsItem {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
    private String summary;    // 기사 요약


    public NewsItem(String title, String originallink, String link, String description, String pubDate) {
        this.title = title;
        this.originallink = originallink;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }

    public String getTitle() { return title; }
    public String getoriginallink() { return originallink; }
    public String getLink() { return link; }
    public String getDescription() { return description; }
    public String getPubDate() { return pubDate; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
}
