package com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.projects;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http.Api;

import org.json.JSONException;
import org.json.JSONObject;

public class ProjectCreateActivity extends AppCompatActivity {

    final static int CREATE_PROJECT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_create);
        final String projectsUrl = getIntent().getStringExtra("projectsUrl");
        findViewById(R.id.projectsCreateB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject projectData = new JSONObject();
                try {
                    projectData.put("name", ((EditText) findViewById(R.id.createProjectET)).getText().toString());
                    Api.post(projectsUrl, projectData, new AbstractContinuation<JSONObject>() {
                        @Override
                        public void then(JSONObject data) {
                            try {
                                Project newProject = new Project(data.getInt("id"), ((EditText) findViewById(R.id.createProjectET)).getText().toString(), projectsUrl);
                                Intent intent = new Intent();
                                intent.putExtra("project", newProject);
                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void fail(Exception e) {
                            setError("No hay error, no hay error, no hay error");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setError(String message) {
        ((TextView) findViewById(R.id.createProjectErrorTV)).setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
