package com.itesm.equipo_x.proyecto_moviles.auth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                String username = ((EditText) findViewById(R.id.registerUsernameET)).getText().toString();
                String password = ((EditText) findViewById(R.id.registerPasswordET)).getText().toString();
                String name = ((EditText) findViewById(R.id.registerNameET)).getText().toString();
                int [] lowLimits = {3, 3, 5};
                int [] upLimits = {71, 25, 31};
                if(!(name.length() > lowLimits[0] && name.length() < upLimits[0])){
                    setError("El nombre debe de ser entre 4 y 70 caracteres.");
                }
                else if(!(username.length() > lowLimits[1] && username.length() < upLimits[1])){
                    setError("El usuario debe de ser entre 4 y 25 caracteres.");
                }
                else if(!(password.length() > lowLimits[2] && password.length() < upLimits[2])){
                    setError("La password debe de ser entre 6 y 30 caracteres.");
                }
                else {
                    try {
                        registerData.put("username", username);
                        registerData.put("password", password);
                        registerData.put("name", name);
                        Api.post(registerUrl, registerData, registerHandler);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
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
