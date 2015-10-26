package com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.projects;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        String projectDetailsUrl = getIntent().getStringExtra("projectDetailsUrl");
        Api.get(projectDetailsUrl, new AbstractContinuation<JSONObject>() {
            @Override
            public void then(JSONObject data) {
                List<String> collaborators = new ArrayList<>();

                try {
                    ((TextView) findViewById(R.id.projectDetailsNameTV)).setText(data.getString("name"));
                    JSONObject collaboratorsResource = data.getJSONObject("_embedded").getJSONObject("collaborators");

                    int total = collaboratorsResource.getInt("total");
                    JSONObject _embeddedCollaborators = collaboratorsResource.getJSONObject("_embedded");
                    for (int i = 0; i < total; i++) {
                        JSONObject collaborator = _embeddedCollaborators.getJSONObject(Integer.toString(i));
                        collaborators.add(collaborator.getString("username"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CollaboratorListAdapter adapter = new CollaboratorListAdapter(getApplicationContext(), R.layout.layout_project, collaborators);
                ((ListView) findViewById(R.id.projectDetailsCollaboratorsLV)).setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
