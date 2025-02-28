package util;

public class ConfigLoader {
    public static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
    public static final String API_KEY = System.getenv("API_KEY");
    public static final String SENDER_EMAIL = System.getenv("SENDER_EMAIL");
    public static final String NAVER_CLIENT_ID = System.getenv("NAVER_CLIENT_ID");
    public static final String NAVER_CLIENT_SECRET = System.getenv("NAVER_CLIENT_SECRET");

    public static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;
}
