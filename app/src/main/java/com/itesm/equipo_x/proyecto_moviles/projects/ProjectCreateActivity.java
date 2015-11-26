package com.itesm.equipo_x.proyecto_moviles.projects;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.auth.LoginActivity;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.common.Http.HttpException;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class ProjectCreateActivity extends AppCompatActivity {

    private static final int CREATE_PROJECT = 0;

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
                    String projectName = ((EditText) findViewById(R.id.createProjectET)).getText().toString();
                    if (projectName.length() < 4 || projectName.length() > 100) {
                        setError("El nombre del proyecto debe de contener entre 4 y 100 caracteres.");
                        return;
                    }

                    projectData.put("name", projectName);
                    Api.post(projectsUrl, projectData, new AbstractContinuation<JSONObject>() {
                        @Override
                        public void then(JSONObject data) {
                            try {
                                Project newProject = new Project(data.getInt("id"), ((EditText) findViewById(R.id.createProjectET)).getText().toString(), data.getJSONObject("_rels").getString("self"));
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

                            if (e instanceof HttpException) {
                                HttpException exception = (HttpException) e;
                                if (exception.getStatusCode() == HttpsURLConnection.HTTP_CONFLICT) {
                                    setError("El proyecto ya existe.");
                                } else {
                                    setError("Hubo un error al contactar al servidor.");
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_create, menu);
        MenuItem text = menu.findItem(R.id.menuProjectCreateUsername);
        text.setTitle(LoginActivity.getCurrentUser());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuProjectCreateLogout:
                LoginActivity.logout(ProjectCreateActivity.this);
                return true;
            case R.id.menuProjectCreateUsername:
                //Missing Profile Link
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
