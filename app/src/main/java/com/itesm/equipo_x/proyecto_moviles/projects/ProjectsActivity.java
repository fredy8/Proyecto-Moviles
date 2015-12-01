package com.itesm.equipo_x.proyecto_moviles.projects;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.auth.LoginActivity;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.profiles.UserProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProjectsActivity extends AppCompatActivity {
    private static final int CREATE_PROJECT = 0;
    public static final int EDIT_PROJECT = 1;
    private ListView projectsLV;
    private String projectsUrl;
    private ProgressBar progressBarLoad;
    private int selectedProjectIndex;
    private final List<Double> coordinates = new ArrayList<>();
    private boolean fetchedProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        projectsLV = ((ListView) findViewById(R.id.projectsProjectsLV));
        progressBarLoad = (ProgressBar)findViewById(R.id.projectsProgressBar);
        progressBarLoad.setVisibility(View.VISIBLE);

        projectsUrl = getIntent().getStringExtra("projectsUrl");

        findViewById(R.id.projectsCreateB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProjectCreateActivity.class);
                intent.putExtra("projectsUrl", projectsUrl);
                startActivityForResult(intent, CREATE_PROJECT);
            }
        });

        getProjectList();
        fetchedProjects = false;

        progressBarLoad.setVisibility(View.GONE);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    coordinates.clear();
                    coordinates.add(location.getLatitude());
                    coordinates.add(location.getLongitude());
                    if (!fetchedProjects) {
                        getProjectList();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

                @Override
                public void onProviderEnabled(String provider) { }

                @Override
                public void onProviderDisabled(String provider) { }
            };

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
                getProjectList();
            }
        } else {
            getProjectList();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == CREATE_PROJECT || requestCode == EDIT_PROJECT) && resultCode == RESULT_OK) {
            getProjectList();
        }
    }

    private void getProjectList() {
        fetchedProjects = true;
        Map<String, String> queryArgs = new HashMap<>();
        if (coordinates.size() != 0) {
            queryArgs.put("lat", Double.toString(coordinates.get(0)));
            queryArgs.put("long", Double.toString(coordinates.get(1)));
        }

        Api.get(projectsUrl, new AbstractContinuation<JSONObject>() {
            @Override
            public void then(JSONObject data) {
                List<Project> projects = new ArrayList<>();
                try {
                    int total = data.getInt("total");
                    JSONObject _embedded = data.getJSONObject("_embedded");
                    for (int i = 0; i < total; i++) {
                        JSONObject project = _embedded.getJSONObject(Integer.toString(i));
                        projects.add(new Project(project.getInt("id"), project.getString("name"), project.getJSONObject("_rels").getString("self"), project.getBoolean("isowner")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ProjectListAdapter adapter = new ProjectListAdapter(getApplicationContext(), R.layout.layout_project, projects, ProjectsActivity.this);
                registerForContextMenu(projectsLV);
                projectsLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View v, int position, long id) {
                        selectedProjectIndex = position;
                        Project selectedProject = (Project) projectsLV.getItemAtPosition(position);

                        if (selectedProject.isOwner()) {
                            projectsLV.showContextMenu();
                        }

                        return true;
                    }
                });

                projectsLV.setAdapter(adapter);
            }
        }, queryArgs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_projects, menu);
        MenuItem text = menu.findItem(R.id.menuProjectsUsername);
        text.setTitle(LoginActivity.getCurrentUser().getUsername());
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_context_project, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            Project project = (Project) projectsLV.getItemAtPosition(selectedProjectIndex);
            Api.delete(project.getProjectDetailsUrl(), new AbstractContinuation<JSONObject>() {
                @Override
                public void fail(Exception e) {
                    Toast.makeText(getApplicationContext(), "Ocurrió un error al borrar el proyecto.", Toast.LENGTH_SHORT);
                }

                @Override
                public void then(JSONObject data) {
                    Toast.makeText(getApplicationContext(), "Se borró el proyecto exitosamente.", Toast.LENGTH_SHORT);
                    getProjectList();
                }
            });
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuProjectsLogout:
                LoginActivity.logout(ProjectsActivity.this);
                return true;
            case R.id.menuProjectsUsername:
                Intent intent = new Intent(ProjectsActivity.this, UserProfileActivity.class);
                intent.putExtra("collaboratorUrl", LoginActivity.getCurrentUser().getUrl());
                ProjectsActivity.this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
