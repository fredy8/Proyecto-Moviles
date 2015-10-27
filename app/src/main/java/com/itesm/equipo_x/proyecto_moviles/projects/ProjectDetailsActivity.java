package com.itesm.equipo_x.proyecto_moviles.projects;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectDetailsActivity extends AppCompatActivity {

    private static final int ADD_COLLABORATOR = 0;
    private ListView collaboratorsLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        collaboratorsLV = (ListView) findViewById(R.id.projectDetailsCollaboratorsLV);
        String projectDetailsUrl = getIntent().getStringExtra("projectDetailsUrl");
        Api.get(projectDetailsUrl, new AbstractContinuation<JSONObject>() {
            @Override
            public void then(final JSONObject data) {
                try {
                    List<String> collaborators = new ArrayList<>();
                    ((TextView) findViewById(R.id.projectDetailsNameTV)).setText(data.getString("name"));
                    final JSONObject collaboratorsResource = data.getJSONObject("_embedded").getJSONObject("collaborators");

                    int total = collaboratorsResource.getInt("total");
                    JSONObject _embeddedCollaborators = collaboratorsResource.getJSONObject("_embedded");
                    for (int i = 0; i < total; i++) {
                        JSONObject collaborator = _embeddedCollaborators.getJSONObject(Integer.toString(i));
                        collaborators.add(collaborator.getString("username"));
                    }

                    CollaboratorListAdapter adapter = new CollaboratorListAdapter(getApplicationContext(), R.layout.layout_project, collaborators);
                    collaboratorsLV.setAdapter(adapter);
                    findViewById(R.id.projectDetailsAddB).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(ProjectDetailsActivity.this, AddCollaboratorActivity.class);
                                intent.putExtra("collaboratorsUrl", collaboratorsResource.getJSONObject("_rels").getString("self"));
                                startActivityForResult(intent, ADD_COLLABORATOR);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_COLLABORATOR && resultCode == RESULT_OK) {
            ((CollaboratorListAdapter) collaboratorsLV.getAdapter()).addCollaborator(data.getStringExtra("collaborator"));
        }
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
