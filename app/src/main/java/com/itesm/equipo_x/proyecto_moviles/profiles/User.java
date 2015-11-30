package com.itesm.equipo_x.proyecto_moviles.profiles;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rrs94 on 18/11/15.
 */
public class User implements Serializable {
    private final String name;
    private final String username;
    private final Bitmap picture = null;
    private final String url;

    public User(String username, String url) {
        this("", username, url);
    }

    public User(String name, String username, String url) {
        this.name = name;
        this.username = username;
        this.url = url;
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
