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

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Continuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.common.Http.HttpException;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

public class AddCollaboratorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_collaborator);

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
                            intent.putExtra("collaborator", collaboratorUsername);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
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
    }

    private void setError(String error) {
        ((TextView) findViewById(R.id.addCollaboratorErrorTV)).setText(error);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_collaborator, menu);
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
