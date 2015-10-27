package com.itesm.equipo_x.proyecto_moviles.auth;

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

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final String registerUrl = getIntent().getStringExtra("registerUrl");

        final Continuation registerHandler = new AbstractContinuation<JSONObject>() {
            @Override
            public void then(JSONObject response) {
                try {
                    String token = response.getString("token");
                    LoginActivity.login(RegisterActivity.this, token, true, new AbstractContinuation<Void>(){
                        @Override
                        public void fail(Exception e) {
                            setError("Hubo un error al contactar al servidor.");
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(Exception e) {
                if (e instanceof HttpException) {
                    HttpException exception = (HttpException) e;
                    if (exception.getStatusCode() == HttpsURLConnection.HTTP_CONFLICT) {
                        setError("Usuario existente.");
                    }
                } else {
                    setError("Hubo un error al contactar al servidor.");
                }
            }
        };

        findViewById(R.id.registerRegisterB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject registerData = new JSONObject();
                try {
                    registerData.put("username", ((EditText) findViewById(R.id.registerUsernameET)).getText().toString());
                    registerData.put("password", ((EditText) findViewById(R.id.registerPasswordET)).getText().toString());
                    Api.post(registerUrl, registerData, registerHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setError(String message) {
        ((TextView) findViewById(R.id.registerErrorTV)).setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
