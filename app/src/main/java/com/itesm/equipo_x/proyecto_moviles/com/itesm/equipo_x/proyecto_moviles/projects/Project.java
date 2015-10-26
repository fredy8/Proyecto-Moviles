package com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.projects;

/**
 * Created by alfredo_altamirano on 10/26/15.
 */
public class Project {

    private final int id;
    private final String name;

    public Project(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
