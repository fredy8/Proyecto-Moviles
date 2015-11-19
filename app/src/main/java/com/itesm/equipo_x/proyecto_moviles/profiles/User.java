package com.itesm.equipo_x.proyecto_moviles.profiles;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rrs94 on 18/11/15.
 */
public class User implements Serializable{
    private final String name;
    private final String username;
    private final Bitmap picture;
    private final String url;

    public User(String n, String u, Bitmap p, String ur){
        this.name = n;
        this.username = u;
        this.picture = p;
        this.url = ur;
    }
    public User(String u, String ur){
        this.name = "";
        this.username = u;
        this.picture = null;
        this.url = ur;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public String getUrl() {
        return url;
    }
}
