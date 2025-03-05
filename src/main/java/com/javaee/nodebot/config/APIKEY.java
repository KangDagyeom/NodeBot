package com.javaee.nodebot.config;

/**
 * @author Hyun
 */
import io.github.cdimascio.dotenv.Dotenv;

public class APIKEY {

    private String key;

    public APIKEY() {
        Dotenv dotenv = Dotenv.load();
        key = dotenv.get("RAPIDAPI_API_KEY");
    }

    public String getKey() {
        return key;
    }
}
