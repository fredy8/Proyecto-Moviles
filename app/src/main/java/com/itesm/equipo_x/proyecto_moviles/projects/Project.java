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
    private final boolean isOwner;

    private final String projectDetailsUrl;

    public Project(int id, String name, String projectDetailsUrl, boolean isOwner) {
        this(id, name, projectDetailsUrl, new ArrayList<String>(), isOwner);
    }

    public Project(int id, String name, String projectDetailsUrl, List<String> collaborators, boolean isOwner) {
        this.id = id;
        this.name = name;
        this.projectDetailsUrl = projectDetailsUrl;
        this.collaborators = Collections.unmodifiableList(collaborators);
        this.isOwner = isOwner;
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

    public boolean isOwner() {
        return isOwner;
    }
}
