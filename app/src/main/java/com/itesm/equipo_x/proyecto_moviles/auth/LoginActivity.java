    // Copyright (C) 2015  Ricardo Rodriguez Sepulveda, Alfredo Altamirano Montealvo, Gabriel Berlanga Serrato

    // This program is free software: you can redistribute it and/or modify
    // it under the terms of the GNU General Public License as published by
    // the Free Software Foundation, either version 3 of the License, or
    // (at your option) any later version.

    // This program is distributed in the hope that it will be useful,
    // but WITHOUT ANY WARRANTY; without even the implied warranty of
    // MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    // GNU General Public License for more details.

    // You should have received a copy of the GNU General Public License
    // along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.itesm.equipo_x.proyecto_moviles.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itesm.equipo_x.proyecto_moviles.profiles.User;
import com.itesm.equipo_x.proyecto_moviles.projects.ProjectsActivity;
import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.common.Continuation;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String PREFERENCES = "loginPreferences";
    private static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences settings = getSharedPreferences(PREFERENCES, 0);
        String accessToken = settings.getString("accessToken", null);
        if (accessToken != null) {
            login(this, accessToken, false, new AbstractContinuation<Void>() {
                @Override
                public void fail(Exception e) {
                    setError("Hubo un error al contactar al servidor.");
                }
            });
        } else {
            Api.get(new AbstractContinuation<JSONObject>() {
                @Override
                public void then(JSONObject api) {
                    registerListeners(api);
                }

                public void fail(Exception e) {
                    setError("Hubo un error al contactar al servidor.");
                    e.printStackTrace();
                }
            });
        }
    }

    private void registerListeners(final JSONObject apiResource) {
        final Continuation loginHandler = new AbstractContinuation<JSONObject>() {
            @Override
            public void then(JSONObject response) {
                try {
                    String token = response.getString("token");
                    login(LoginActivity.this, token, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(Exception e) {
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

    private void setError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
        editor.commit();
        currentUser = null;
        Api.removeAccessToken();
        activity.startActivity(new Intent(activity, LoginActivity.class));
    }

    public static void login(Activity activity, String token, boolean saveToken) {
        login(activity, token, saveToken, new AbstractContinuation<Void>());
    }

    public static void login(final Activity activity, String token, boolean saveToken, final Continuation<Void> continuation) {
        if (saveToken) {
            SharedPreferences settings = activity.getSharedPreferences(PREFERENCES, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("accessToken", token);
            editor.commit();
        }

        Api.setAccessToken(token);

        Api.get(new AbstractContinuation<JSONObject>() {
            @Override
            public void then(JSONObject apiResource) {
                try {
                    Intent intent = new Intent(activity, ProjectsActivity.class);
                    JSONObject profile = apiResource.getJSONObject("_embedded").getJSONObject("profile");
                    String username = profile.getString("username");
                    String name = profile.getString("name");
                    String profileUrl = apiResource.getJSONObject("_rels").getString("profile");
                    currentUser = new User(name, username, profileUrl);
                    intent.putExtra("projectsUrl", apiResource.getJSONObject("_rels").getString("projects"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                } catch (JSONException e) {
                    continuation.fail(e);
                }
            }
        });
    }

    public static User getCurrentUser() {
        return currentUser;
    }

}
