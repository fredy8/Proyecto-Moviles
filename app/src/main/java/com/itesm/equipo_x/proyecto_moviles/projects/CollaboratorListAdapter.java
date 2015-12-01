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
import com.itesm.equipo_x.proyecto_moviles.profiles.User;
import com.itesm.equipo_x.proyecto_moviles.profiles.UserProfileActivity;

import java.util.List;

/**
 * Created by alfredo_altamirano on 10/26/15.
 */
public class CollaboratorListAdapter extends ArrayAdapter<User> {

    private Context context;
    private int resource;
    private List<User> collaborators;
    private Activity activity;
    private Boolean isOwner;

    public CollaboratorListAdapter(Context context, int resource, List<User> collaborators, Activity activity, Boolean isOwner) {
        super(context, resource, collaborators);
        this.context = context;
        this.resource = resource;
        this.collaborators = collaborators;
        this.activity = activity;
        this.isOwner = isOwner;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(resource, parent, false);
        final User user = collaborators.get(position);

        ((TextView)row.findViewById(R.id.projectNameTV)).setText(collaborators.get(position).getUsername());
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, UserProfileActivity.class);
                intent.putExtra("collaboratorUrl", user.getUrl());
                CollaboratorListAdapter.this.activity.startActivity(intent);
            }
        });
        return row;
    }

    public void addCollaborator(User collaborator) {
        this.collaborators.add(collaborator);
        this.notifyDataSetChanged();
    }

}
