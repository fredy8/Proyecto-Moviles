package com.itesm.equipo_x.proyecto_moviles.common.Http;

import com.itesm.equipo_x.proyecto_moviles.common.Continuation;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alfredo_altamirano on 10/25/15.
 */
public class Api {

    private static Map<String, String> headers = new HashMap<>();

    private static final String ROOT = "http://moviles-staging.cloudapp.net:3000";

    private Api() {}

    public static void setAccessToken(String accessToken) {
        headers.put("x-access-token", accessToken);
    }

    public static void get(Continuation<JSONObject> continuation) {
        get(ROOT, continuation);
    }

    public static void get(String url, Continuation<JSONObject> continuation) {
        JsonHttpRequest.request(url, headers, "GET", null, continuation);
    }

    public static void get(String url, Continuation<JSONObject> continuation, Map<String, String> queryArgs) {
        String queryString = "";
        try {
            for(Map.Entry<String, String> entry : queryArgs.entrySet()) {
                String arg = URLEncoder.encode(entry.getKey(), "utf-8") + '=' + URLEncoder.encode(entry.getValue(), "utf-8");
                if (queryString.length() == 0) {
                    queryString += "?" + arg;
                } else {
                    queryString += "&" + arg;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        url += queryString;
        JsonHttpRequest.request(url, headers, "GET", null, continuation);
    }

    public static void post(String url, JSONObject data, Continuation<JSONObject> continuation) {
        JsonHttpRequest.request(url, headers, "POST", data, continuation);
    }

    public static void put(String url, JSONObject data, Continuation<JSONObject> continuation) {
        JsonHttpRequest.request(url, headers, "PUT", data, continuation);
    }

    public static void delete(String url, Continuation<JSONObject> continuation) {
        JsonHttpRequest.request(url, headers, "DELETE", null, continuation);
    }

    public static void removeAccessToken() {
        headers.remove("x-access-token");
    }
}
