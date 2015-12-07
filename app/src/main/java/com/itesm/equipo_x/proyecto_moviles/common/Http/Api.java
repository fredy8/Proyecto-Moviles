    // Copyright (C) 2015  Ricardo Rodriguez Sepulveda, Alfredo Altamirano Montealvo, Gabriel Berlanga Serrato

    // This program is free software: you can redistribute it and/or modify
    // it under the terms of the GNU General Public License as published by
    // the Free Software Foundation, either version 3 of the License, or
    // (at your option) any later version.

    // This program is distributed in the hope that it will be useful,
    // but WITHOUT ANY WARRANTY; without even the implied warranty of
    // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    // GNU General Public License for more details.

    // You should have received a copy of the GNU General Public License
    // along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.itesm.equipo_x.proyecto_moviles.common.Http;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.itesm.equipo_x.proyecto_moviles.common.Continuation;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
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

    private static boolean isNetworkAvailable(Activity context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void get(Continuation<JSONObject> continuation, Activity context) {
        get(ROOT, continuation, context);
    }

    public static void get(String url, Continuation<JSONObject> continuation, Activity context) {
        if (!isNetworkAvailable(context)) {
            continuation.fail(new SocketTimeoutException());
            return;
        }

        JsonHttpRequest.request(url, headers, "GET", null, continuation);
    }

    public static void get(String url, Continuation<JSONObject> continuation, Map<String, String> queryArgs, Activity context) {
        if (!isNetworkAvailable(context)) {
            continuation.fail(new SocketTimeoutException());
            return;
        }

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

    public static void post(String url, JSONObject data, Continuation<JSONObject> continuation, Activity context) {
        if (!isNetworkAvailable(context)) {
            continuation.fail(new SocketTimeoutException());
            return;
        }

        JsonHttpRequest.request(url, headers, "POST", data, continuation);
    }

    public static void put(String url, JSONObject data, Continuation<JSONObject> continuation, Activity context) {
        if (!isNetworkAvailable(context)) {
            continuation.fail(new SocketTimeoutException());
            return;
        }

        JsonHttpRequest.request(url, headers, "PUT", data, continuation);
    }

    public static void delete(String url, Continuation<JSONObject> continuation, Activity context) {
        if (!isNetworkAvailable(context)) {
            continuation.fail(new SocketTimeoutException());
            return;
        }

        JsonHttpRequest.request(url, headers, "DELETE", null, continuation);
    }

    public static void removeAccessToken() {
        headers.remove("x-access-token");
    }
}
