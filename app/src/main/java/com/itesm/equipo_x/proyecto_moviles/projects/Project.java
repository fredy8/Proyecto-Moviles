package com.itesm.equipo_x.proyecto_moviles.projects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alfredo_altamirano on 10/26/15.
 */
public class Project implements Serializable {

    private final int id;
    private final String name;
    private final List<String> collaborators;

    private final String projectDetailsUrl;

    public Project(int id, String name, String projectDetailsUrl) {
        this(id, name, projectDetailsUrl, new ArrayList<String>());
    }

    public Project(int id, String name, String projectDetailsUrl, List<String> collaborators) {
        this.id = id;
        this.name = name;
        this.projectDetailsUrl = projectDetailsUrl;
        this.collaborators = Collections.unmodifiableList(collaborators);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getCollaborators() {
        return this.collaborators;
    }

    public String getProjectDetailsUrl() {
        return projectDetailsUrl;
    }
}
