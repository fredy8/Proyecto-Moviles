package com.itesm.equipo_x.proyecto_moviles.common;

/**
 * Created by alfredo_altamirano on 10/25/15.
 */
public interface Continuation<T> {

    public void then(T data);

    public void fail(Exception e);

}
