package com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http;

import com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http.JsonHttpRequest;
import com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http.RequestHandler;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alfredo_altamirano on 10/25/15.
 */
public class Api {

    private static Map<String, String> headers = new HashMap<>();

    public static final String ROOT = "http://moviles-staging.cloudapp.net:3000";

    private Api() {}

    public static void setAccessToken(String accessToken) {
        headers.put("x-access-token", accessToken);
    }

    public static void get(String url, RequestHandler handler) {
        JsonHttpRequest.request(url, headers, "GET", null, handler);
    }

    public static void post(String url, JSONObject data, RequestHandler handler) {
        JsonHttpRequest.request(url, headers, "POST", data, handler);
    }

    public static void removeAccessToken() {
        headers.remove("x-access-token");
    }
}
