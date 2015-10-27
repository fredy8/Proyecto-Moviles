package com.itesm.equipo_x.proyecto_moviles.common.Http;

import org.json.JSONObject;

/**
 * Created by alfredo_altamirano on 10/25/15.
 */
public class HttpException extends RuntimeException {

    private int statusCode;

    private JSONObject responseBody;

    public HttpException(int statusCode, JSONObject responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public JSONObject getResponseBody() {
        return responseBody;
    }
}
