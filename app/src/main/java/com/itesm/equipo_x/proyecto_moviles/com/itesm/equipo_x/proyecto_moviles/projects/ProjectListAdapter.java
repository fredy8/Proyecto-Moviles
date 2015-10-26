package com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.projects;

import android.content.Context;
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

    public ProjectListAdapter(Context context, int resource, List<Project> projects) {
        super(context, resource, projects);
        this.context = context;
        this.resource = resource;
        this.projects = projects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(resource, parent, false);

        ((TextView)row.findViewById(R.id.projectNameTV)).setText(projects.get(position).getName());
        return row;
    }

}
