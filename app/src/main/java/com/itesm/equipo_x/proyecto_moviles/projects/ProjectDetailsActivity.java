package com.itesm.equipo_x.proyecto_moviles.projects;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.auth.LoginActivity;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.common.Http.HttpException;
import com.itesm.equipo_x.proyecto_moviles.profiles.User;
import com.itesm.equipo_x.proyecto_moviles.projects.evaluations.Evaluation;
import com.itesm.equipo_x.proyecto_moviles.projects.evaluations.EvaluationListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class ProjectDetailsActivity extends AppCompatActivity {

    private static final int ADD_COLLABORATOR = 0;
    private ListView collaboratorsLV;
    private Boolean isOwner;
    private Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        collaboratorsLV = (ListView) findViewById(R.id.projectDetailsCollaboratorsLV);
        final String projectDetailsUrl = getIntent().getStringExtra("projectDetailsUrl");

        isOwner = false;
        editButton = (Button)findViewById(R.id.projectEditNameB);

        final AbstractContinuation<JSONObject> evaluationHandler = new AbstractContinuation<JSONObject>() {
            @Override
            public void then(JSONObject data) {
                try {
                    int total = data.getInt("total");
                    List<Evaluation> evaluations = new ArrayList<>();
                    JSONObject embedded = data.getJSONObject("_embedded");
                    for(int i = 0; i<total; i++){
                        JSONObject evaluation = embedded.getJSONObject(Integer.toString(i));
                        String name = evaluation.getString("name");
                        int type = evaluation.getInt("type");
                        String url = evaluation.getJSONObject("_rels").getString("self");
                        evaluations.add(new Evaluation(name, type, url));
                    }

                    ((ListView)findViewById(R.id.projectDetailsEvaluationsLV)).setAdapter(new EvaluationListAdapter(getApplicationContext(), R.layout.layout_project, evaluations, ProjectDetailsActivity.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void fail(Exception e) {
                super.fail(e);
            }
        };

        Api.get(projectDetailsUrl, new AbstractContinuation<JSONObject>() {
            @Override
            public void then(final JSONObject data) {
                try {
                    Api.get(data.getJSONObject("_rels").getString("evaluations"), evaluationHandler);
                    List<User> collaborators = new ArrayList<>();
                    isOwner = data.getBoolean("isOwner");
                    if(isOwner){
                        editButton.setVisibility(View.VISIBLE);
                    }
                    ((TextView) findViewById(R.id.projectDetailsNameTV)).setText(data.getString("name"));
                    final JSONObject collaboratorsResource = data.getJSONObject("_embedded").getJSONObject("collaborators");

                    int total = collaboratorsResource.getInt("total");
                    JSONObject _embeddedCollaborators = collaboratorsResource.getJSONObject("_embedded");
                    for (int i = 0; i < total; i++) {
                        JSONObject collaborator = _embeddedCollaborators.getJSONObject(Integer.toString(i));
                        String collaboratorUrl = collaborator.getJSONObject("_rels").getString("self");
                        collaborators.add(new User(collaborator.getString("username"), collaboratorUrl));
                    }

                    CollaboratorListAdapter adapter = new CollaboratorListAdapter(getApplicationContext(), R.layout.layout_project, collaborators, ProjectDetailsActivity.this);
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
        findViewById(R.id.projectEditNameB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ProjectDetailsActivity.this);

                alert.setTitle("Editar Nombre de Proyecto");
                alert.setMessage("Introducir nuevo nombre de proyecto");
                final EditText input = new EditText(ProjectDetailsActivity.this);
                alert.setView(input);
                final String projectsUrl = getIntent().getStringExtra("projectsUrl");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        JSONObject projectData = new JSONObject();
                        try {
                            projectData.put("name", input.getText().toString());
                            Api.put(projectDetailsUrl, projectData, new AbstractContinuation<JSONObject>() {
                                @Override
                                public void then(JSONObject data) {
                                    setResult(Activity.RESULT_OK);
                                    finish();
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

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });
        if(isOwner){
            editButton.setVisibility(View.VISIBLE);
        }
        else{
            editButton.setVisibility(View.GONE);
        }
    }

    private void setError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_COLLABORATOR && resultCode == RESULT_OK) {
            ((CollaboratorListAdapter) collaboratorsLV.getAdapter()).addCollaborator((User) data.getSerializableExtra("collaborator"));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_details, menu);
        MenuItem text = menu.findItem(R.id.menuProjectDetailsUsername);
        text.setTitle(LoginActivity.getCurrentUser());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuProjectDetailsLogout:
                LoginActivity.logout(ProjectDetailsActivity.this);
                return true;
            case R.id.menuProjectDetailsUsername:
                //Missing Profile Link
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
