package com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http;

import org.json.JSONObject;

/**
 * Created by alfredo_altamirano on 10/25/15.
 */
public interface Continuation<T> {

    public void then(T data);

    public void fail(Exception e);

}
