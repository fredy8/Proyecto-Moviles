package com.itesm.equipo_x.proyecto_moviles.projects;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.auth.LoginActivity;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        findViewById(R.id.projectsLogoutB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.logout(ProjectsActivity.this);
            }
        });

        String projectsUrl = getIntent().getStringExtra("projectsUrl");
        Api.get(projectsUrl, new AbstractContinuation<JSONObject>() {
            @Override
            public void then(JSONObject data) {
                List<Project> projects = new ArrayList<>();
                try {
                    int total = data.getInt("total");
                    JSONObject _embedded = data.getJSONObject("_embedded");
                    for (int i = 0; i < total; i++) {
                        JSONObject project = _embedded.getJSONObject(Integer.toString(i));
                        projects.add(new Project(project.getInt("id"), project.getString("name"), project.getJSONObject("_rels").getString("self")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ProjectListAdapter adapter = new ProjectListAdapter(getApplicationContext(), R.layout.layout_project, projects, ProjectsActivity.this);
                ((ListView) findViewById(R.id.projectsProjectsLV)).setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_projects, menu);
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
