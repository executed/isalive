package com.devserbyn.isalive.utility;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebUtility {

    private WebUtility() {}

    public static JSONObject readJsonFromUrl(String link, String reqMethod) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(link).openConnection();
        connection.setRequestMethod(reqMethod);
        connection.setConnectTimeout(2_000);
        try (InputStream is = connection.getInputStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    public static boolean validateURL(String input) {
        try {
            new URL(input).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
