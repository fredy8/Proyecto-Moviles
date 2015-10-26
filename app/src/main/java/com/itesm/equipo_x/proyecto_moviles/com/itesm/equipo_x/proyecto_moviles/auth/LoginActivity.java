package com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.ProjectsActivity;
import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.com.itesm.equipo_x.proyecto_moviles.common.Http.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private JSONObject apiResource;
    private static final String PREFERENCES = "loginPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
        String accessToken = settings.getString("accessToken", null);
        if (accessToken != null) {
            Api.setAccessToken(accessToken);
        }

        getApi();
    }

    private void registerListeners() {
        final RequestHandler loginHandler = new RequestHandler() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String token = response.getString("token");
                    SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("accessToken", token);
                    editor.commit();
                    Api.setAccessToken(token);
                    getApi();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                setError("Usuario o contraseña incorrecto.");
            }
        };

        findViewById(R.id.loginLoginB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject loginData = new JSONObject();
                try {
                    loginData.put("username", ((EditText) findViewById(R.id.loginUsernameET)).getText().toString());
                    loginData.put("password", ((EditText) findViewById(R.id.loginPasswordET)).getText().toString());
                    Api.post(apiResource.getJSONObject("_rels").getString("login"), loginData, loginHandler);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.loginRegisterB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("registerUrl", apiResource.getJSONObject("_rels").getString("register"));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getApi() {
        Api.get(Api.ROOT, new RequestHandler() {
            @Override
            public void onResponse(JSONObject response) {
                apiResource = response;
                String username = null;
                try {
                    username = apiResource.getJSONObject("user").getString("username");
                    Intent intent = new Intent(LoginActivity.this, ProjectsActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                } catch (JSONException e) {
                    registerListeners();
                }
            }

            @Override
            public void onError(Exception e) {
                setError("Hubo un error al contactar al servidor.");
                e.printStackTrace();
            }
        });
    }

    private void setError(String message) {
        ((TextView) findViewById(R.id.loginErrorTV)).setText(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public static void logout(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("accessToken", null);
        Api.removeAccessToken();
        editor.commit();
    }
}
