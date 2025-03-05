package com.javaee.nodebot.config;

/**
 * @author Hyun
 */
public class APIKEY {

    private String key;

    public APIKEY() {
        key = System.getenv("RAPIDAPI_API_KEY"); // Lấy API key từ biến môi trường
    }

    public String getKey() {
        return key;
    }
}
