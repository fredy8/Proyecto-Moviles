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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.R;

import java.util.List;

/**
 * Created by alfredo_altamirano on 10/26/15.
 */
public class ProjectListAdapter extends ArrayAdapter<Project> {

    private Context context;
    private int resource;
    private List<Project> projects;
    private Activity activity;

    public ProjectListAdapter(Context context, int resource, List<Project> projects, Activity activity) {
        super(context, resource, projects);
        this.context = context;
        this.resource = resource;
        this.projects = projects;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(resource, parent, false);

        final Project project = projects.get(position);
        ((TextView)row.findViewById(R.id.projectNameTV)).setText(project.getName() + (project.isOwner()? "*" : ""));
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProjectDetailsActivity.class);
                intent.putExtra("projectDetailsUrl", project.getProjectDetailsUrl());
                ProjectListAdapter.this.activity.startActivityForResult(intent, ProjectsActivity.EDIT_PROJECT);
            }
        });
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                row.showContextMenu();
                return true;
            }
        });
        return row;
    }

    public void addProject(Project project) {
        this.projects.add(project);
        this.notifyDataSetChanged();
    }

}
