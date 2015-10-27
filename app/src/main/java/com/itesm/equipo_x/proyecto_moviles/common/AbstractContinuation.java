package com.itesm.equipo_x.proyecto_moviles.common;

/**
 * Created by alfredo_altamirano on 10/25/15.
 */
public class AbstractContinuation<T> implements Continuation<T> {


    @Override
    public void then(T data) {

    }

    @Override
    public void fail(Exception e) {
        e.printStackTrace();
    }
}
