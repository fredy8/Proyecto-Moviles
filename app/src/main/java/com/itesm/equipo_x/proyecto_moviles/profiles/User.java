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
