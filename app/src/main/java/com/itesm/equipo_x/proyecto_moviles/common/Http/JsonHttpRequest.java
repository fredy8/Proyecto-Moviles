package com.itesm.equipo_x.proyecto_moviles.common.Http;

import android.os.AsyncTask;

import com.itesm.equipo_x.proyecto_moviles.common.Continuation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by alfredo_altamirano on 10/25/15.
 */
class JsonHttpRequest extends AsyncTask<String, Void, JSONObject> {

    private String url, method;
    private Map<String, String> headers;
    private JSONObject data;
    private Continuation handler;
    private Exception error;

    private JsonHttpRequest(String url, Map<String, String> headers, String method, JSONObject data, Continuation handler) {
        this.url = url;
        this.headers = headers;
        this.method = method;
        this.data = data;
        this.handler = handler;
    }

    private static JSONObject readStream(InputStream in) throws IOException, JSONException {
        BufferedReader reader = null;
        StringBuffer response = null;
        JSONObject object = null;

        try {
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            response = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            object = new JSONObject(response.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return object;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject jsonObject = null;
        method = method.toUpperCase();

        HttpURLConnection httpUrlConnection = null;

        try {
            URL myUrl = new URL(url);
            httpUrlConnection = (HttpURLConnection) myUrl.openConnection();

            httpUrlConnection.setRequestMethod(method);
            for(Map.Entry<String, String> header : headers.entrySet()) {
                httpUrlConnection.setRequestProperty(header.getKey(), header.getValue());
            }

            httpUrlConnection.setRequestProperty("Content-Type", "application/json");
            httpUrlConnection.setRequestMethod(method);

            if (!method.equals("GET")) {
                httpUrlConnection.setDoOutput(true);
                OutputStream os = httpUrlConnection.getOutputStream();
                os.write(data.toString().getBytes("UTF-8"));
                os.close();
            }

            httpUrlConnection.connect();

            int responseCode = httpUrlConnection.getResponseCode();

            if (responseCode < 400) {
                if (responseCode == HttpsURLConnection.HTTP_NO_CONTENT) {
                    jsonObject = new JSONObject();
                } else {
                    jsonObject = readStream(httpUrlConnection.getInputStream());
                }
            } else {
                JSONObject body = readStream(httpUrlConnection.getErrorStream());
                error = new HttpException(responseCode, body);
            }
        } catch (Exception e) {
            error = e;
        } finally {
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
        }

        return jsonObject;
    }

    public void onPostExecute(JSONObject result) {
        if (error != null) {
            handler.fail(error);
        } else {
            handler.then(result);
        }
    }

    public static void request(String url, Map<String, String> headers, String method, JSONObject data, Continuation handler) {
        new JsonHttpRequest(url, headers, method, data, handler).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
