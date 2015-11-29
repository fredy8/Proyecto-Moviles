package com.itesm.equipo_x.proyecto_moviles.profiles;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.auth.LoginActivity;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.common.Http.HttpException;
import com.itesm.equipo_x.proyecto_moviles.projects.AddCollaboratorActivity;
import com.itesm.equipo_x.proyecto_moviles.projects.CollaboratorListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class UserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 0;
    private Boolean correctUser;
    private Button editPictureButton;
    private ProgressBar progressBarLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        progressBarLoad = (ProgressBar)findViewById(R.id.userProfileProgressBar);

        final String collaboratorUrl = getIntent().getStringExtra("collaboratorUrl");
        correctUser = false;
        editPictureButton = (Button)findViewById(R.id.userProfilePictureB);
        progressBarLoad.setVisibility(View.VISIBLE);

        Api.get(collaboratorUrl, new AbstractContinuation<JSONObject>() {
            @Override
            public void then(final JSONObject data) {
                try {
                    if(data.getString("username").equals(LoginActivity.getCurrentUser().getUsername())){
                        editPictureButton.setVisibility(View.VISIBLE);
                        correctUser = true;
                    }
                    ((TextView) findViewById(R.id.userProfileNameTV)).setText(data.getString("name"));
                    ((TextView) findViewById(R.id.userProfileUserNameTV)).setText(data.getString("username"));
                    byte[] decodedString = Base64.decode(data.getString("profilePicture"), Base64.DEFAULT);
                    Bitmap bitmapPicture = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    ((ImageView) findViewById(R.id.userProfilePictureIV)).setImageBitmap(bitmapPicture);
                    progressBarLoad.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.userProfilePictureB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
        if(correctUser){
            editPictureButton.setVisibility(View.VISIBLE);
        }
        else{
            editPictureButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        if (req == REQUEST_IMAGE_CAPTURE) {
            if (res == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap image = (Bitmap) extras.get("data");
                ((ImageView) findViewById(R.id.userProfilePictureIV)).setImageBitmap(image);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                JSONObject profileData = new JSONObject();
                final String collaboratorUrl = getIntent().getStringExtra("collaboratorUrl");
                try {
                    profileData.put("profilePicture", encoded);
                    Api.put(collaboratorUrl, profileData, new AbstractContinuation<JSONObject>() {
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
                                    setError("Hubo un error al contactar al servidor.");
                                }
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        MenuItem text = menu.findItem(R.id.menuUserProfileUsername);
        text.setTitle(LoginActivity.getCurrentUser().getUsername());
        return true;
    }

    private void setError(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menuUserProfileLogout:
                LoginActivity.logout(UserProfileActivity.this);
                return true;
            case R.id.menuUserProfileUsername:
                //Missing Profile Link
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
