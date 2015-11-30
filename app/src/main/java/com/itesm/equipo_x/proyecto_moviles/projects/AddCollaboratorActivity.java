package com.itesm.equipo_x.proyecto_moviles.projects;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.auth.LoginActivity;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Continuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.common.Http.HttpException;
import com.itesm.equipo_x.proyecto_moviles.profiles.User;
import com.itesm.equipo_x.proyecto_moviles.profiles.UserProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class AddCollaboratorActivity extends AppCompatActivity {

    private ProgressBar progressBarLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collaborator);
        progressBarLoad = (ProgressBar) findViewById(R.id.addCollaboratorProgressBar);
        progressBarLoad.setVisibility(View.VISIBLE);

        final String collaboratorsUrl = getIntent().getStringExtra("collaboratorsUrl");
        findViewById(R.id.addCollaboratorAddCollaboratorB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject addCollaboratorData = new JSONObject();
                    final String collaboratorUsername = ((EditText) findViewById(R.id.addCollaboratorUsernameET)).getText().toString();
                    addCollaboratorData.put("collaborator", collaboratorUsername);
                    Api.post(collaboratorsUrl, addCollaboratorData, new AbstractContinuation<JSONObject>() {
                        @Override
                        public void then(JSONObject data) {
                            Intent intent = new Intent();
                            try {
                                intent.putExtra("collaborator", new User(collaboratorUsername, data.getJSONObject("_rels").getString("self")));
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
                                if (exception.getStatusCode() == HttpsURLConnection.HTTP_NOT_FOUND) {
                                    setError("No se encontró el colaborador.");
                                } else if (exception.getStatusCode() == HttpsURLConnection.HTTP_CONFLICT) {
                                    setError("El usuario ya es un colaborador.");
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
        progressBarLoad.setVisibility(View.GONE);
    }

    private void setError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_collaborator, menu);
        MenuItem text = menu.findItem(R.id.menuAddCollaboratorUsername);
        text.setTitle(LoginActivity.getCurrentUser().getUsername());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuAddCollaboratorLogout:
                LoginActivity.logout(AddCollaboratorActivity.this);
                return true;
            case R.id.menuAddCollaboratorUsername:
                Intent intent = new Intent(AddCollaboratorActivity.this, UserProfileActivity.class);
                intent.putExtra("collaboratorUrl", LoginActivity.getCurrentUser().getUrl());
                AddCollaboratorActivity.this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
