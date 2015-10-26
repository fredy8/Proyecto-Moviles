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
public class CollaboratorListAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private List<String> collaborators;

    public CollaboratorListAdapter(Context context, int resource, List<String> collaborators) {
        super(context, resource, collaborators);
        this.context = context;
        this.resource = resource;
        this.collaborators = collaborators;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(resource, parent, false);

        ((TextView)row.findViewById(R.id.projectNameTV)).setText(collaborators.get(position));
        return row;
    }

}
