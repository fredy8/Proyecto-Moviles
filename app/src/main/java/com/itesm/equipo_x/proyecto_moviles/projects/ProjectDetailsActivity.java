package com.itesm.equipo_x.proyecto_moviles.projects;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.auth.LoginActivity;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.common.Http.HttpException;
import com.itesm.equipo_x.proyecto_moviles.profiles.User;
import com.itesm.equipo_x.proyecto_moviles.profiles.UserProfileActivity;
import com.itesm.equipo_x.proyecto_moviles.projects.evaluations.Evaluation;
import com.itesm.equipo_x.proyecto_moviles.projects.evaluations.EvaluationActivity;
import com.itesm.equipo_x.proyecto_moviles.projects.evaluations.EvaluationListAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ProjectDetailsActivity extends AppCompatActivity {

    private static final int ADD_COLLABORATOR = 0, ADD_EVALUATION = 1;
    private ListView collaboratorsLV;
    private Boolean isOwner;
    private Button editButton;
    private String projectDetailsUrl;
    private ProgressBar progressBarLoad;
    private final List<Double> coordinates = new ArrayList<>();
    private ListView evaluationsLV;
    private String acc;
    private String projectName;
    private Bitmap picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectName = "proyecto";
        acc = "NA";
        setContentView(R.layout.activity_project_details);
        evaluationsLV = (ListView) findViewById(R.id.projectDetailsEvaluationsLV);
        progressBarLoad = (ProgressBar)findViewById(R.id.projectDetailsProgressBar);
        progressBarLoad.setVisibility(View.VISIBLE);

        collaboratorsLV = (ListView) findViewById(R.id.projectDetailsCollaboratorsLV);
        projectDetailsUrl = getIntent().getStringExtra("projectDetailsUrl");

        isOwner = false;
        editButton = (Button)findViewById(R.id.projectEditNameB);

        fetchProject();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    coordinates.clear();
                    coordinates.add(location.getLatitude());
                    coordinates.add(location.getLongitude());
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
            }
        }
        findViewById(R.id.fbB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareImage();
                } catch (Exception e) {
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

    private void fetchProject() {
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
                    EvaluationListAdapter adapter = new EvaluationListAdapter(getApplicationContext(), R.layout.layout_project, evaluations, ProjectDetailsActivity.this);
                    registerForContextMenu(evaluationsLV);
                    evaluationsLV.setAdapter(adapter);
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
                    final String evaluationUrl = data.getJSONObject("_rels").getString("evaluations");

                    Map<String, String> queryArgs = new HashMap<>();
                    if (coordinates.size() != 0) {
                        queryArgs.put("lat", Double.toString(coordinates.get(0)));
                        queryArgs.put("long", Double.toString(coordinates.get(1)));
                    }

                    Api.get(evaluationUrl, evaluationHandler, queryArgs);
                    findViewById(R.id.projectDetailsAddEvaluationB).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProjectDetailsActivity.this, EvaluationActivity.class);
                            intent.putExtra("evaluationUrl", evaluationUrl);
                            intent.putExtra("action", EvaluationActivity.CREATE);
                            startActivityForResult(intent, ADD_EVALUATION);
                        }
                    });
                    final String reportUrl = data.getJSONObject("_rels").getString("report");
                    Api.get(reportUrl, new AbstractContinuation<JSONObject>() {
                        @Override
                        public void then(JSONObject data) {
                            try {
                                if (data.has("accessibility") && !data.isNull("accessibility")) {
                                    double num = data.getDouble("accessibility");
                                    num = num *100;
                                    acc = String.valueOf(new DecimalFormat("#.##").format(num));
                                    acc += "%";
                                    ((TextView) findViewById(R.id.projectDetailsPercentageTV)).setText(acc);
                                    if(num<50){
                                        ((TextView) findViewById(R.id.projectDetailsPercentageTV)).setTextColor(Color.parseColor("#FF0000"));
                                    }
                                    else if(num < 80){
                                        ((TextView) findViewById(R.id.projectDetailsPercentageTV)).setTextColor(Color.parseColor("#d6d618"));
                                    }
                                    else {
                                        ((TextView) findViewById(R.id.projectDetailsPercentageTV)).setTextColor(Color.parseColor("#008000"));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    List<User> collaborators = new ArrayList<>();
                    isOwner = data.getBoolean("isOwner");
                    if (isOwner) {
                        editButton.setVisibility(View.VISIBLE);
                    }

                    //Add Picture
                    byte[] decodedString = Base64.decode(data.getString("picture"), Base64.DEFAULT);
                    Bitmap bitmapPicture = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    picture = bitmapPicture;
                    ((ImageView) findViewById(R.id.projectDetailsIV)).setImageBitmap(bitmapPicture);
                    projectName = data.getString("name");
                    ((TextView) findViewById(R.id.projectDetailsNameTV)).setText(projectName);
                    final JSONObject collaboratorsResource = data.getJSONObject("_embedded").getJSONObject("collaborators");

                    int total = collaboratorsResource.getInt("total");
                    JSONObject _embeddedCollaborators = collaboratorsResource.getJSONObject("_embedded");
                    for (int i = 0; i < total; i++) {
                        JSONObject collaborator = _embeddedCollaborators.getJSONObject(Integer.toString(i));
                        String collaboratorUrl = collaborator.getJSONObject("_rels").getString("self");
                        collaborators.add(new User(collaborator.getString("username"), collaboratorUrl));
                    }

                    CollaboratorListAdapter adapter = new CollaboratorListAdapter(getApplicationContext(), R.layout.layout_project, collaborators, ProjectDetailsActivity.this, isOwner);
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
                    progressBarLoad.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
    }

    private void shareImage() {
        ShareDialog shareDialog;
        FacebookSdk.sdkInitialize(getApplicationContext());
        shareDialog = new ShareDialog(this);
        String caption = "El porcentaje de accesibilidad de " + projectName + " es " + acc;

        Bitmap bitmap = picture;
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption(caption)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareLinkContent content2 =	new	ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com")).build();
        LoginManager.getInstance().logInWithPublishPermissions(
                ProjectDetailsActivity.this,
                Arrays.asList("publish_actions"));
        if(shareDialog.canShow(ShareLinkContent.class)){
            shareDialog.show(content2);
        }
        else{
            Toast.makeText(getApplicationContext(), "You cannot share photos :(", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_COLLABORATOR) {
                ((CollaboratorListAdapter) collaboratorsLV.getAdapter()).addCollaborator((User) data.getSerializableExtra("collaborator"));
            } else if (requestCode == ADD_EVALUATION) {
                fetchProject();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_details, menu);
        MenuItem text = menu.findItem(R.id.menuProjectDetailsUsername);
        text.setTitle(LoginActivity.getCurrentUser().getUsername());
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_context_evaluation, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deleteEvaluation) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Evaluation evaluation = (Evaluation) evaluationsLV.getItemAtPosition(info.position);
            Api.delete(evaluation.getEvaluationUrl(), new AbstractContinuation<JSONObject>() {
                @Override
                public void fail(Exception e) {
                    Toast.makeText(getApplicationContext(), "Ocurrió un error al borrar el diagnostico.", Toast.LENGTH_SHORT);
                }

                @Override
                public void then(JSONObject data) {
                    Toast.makeText(getApplicationContext(), "Se borró el diagnostico exitosamente.", Toast.LENGTH_SHORT);
                    fetchProject();
                }
            });
            return true;
        }
        else if(item.getItemId() == R.id.editEvaluation){
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Evaluation evaluation = (Evaluation) evaluationsLV.getItemAtPosition(info.position);
            Intent intent = new Intent(ProjectDetailsActivity.this, EvaluationActivity.class);
            intent.putExtra("evaluationUrl", evaluation.getEvaluationUrl());
            intent.putExtra("action", EvaluationActivity.EDIT);
            startActivityForResult(intent, ADD_EVALUATION);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuProjectDetailsLogout:
                LoginActivity.logout(ProjectDetailsActivity.this);
                return true;
            case R.id.menuProjectDetailsUsername:
                Intent intent = new Intent(ProjectDetailsActivity.this, UserProfileActivity.class);
                intent.putExtra("collaboratorUrl", LoginActivity.getCurrentUser().getUrl());
                ProjectDetailsActivity.this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
