package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.auth.LoginActivity;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;
import com.itesm.equipo_x.proyecto_moviles.common.Http.HttpException;
import com.itesm.equipo_x.proyecto_moviles.profiles.UserProfileActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class EvaluationActivity extends AppCompatActivity {

    public static final int VIEW = 0;
    public static final int EDIT = 1;
    public static final int CREATE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 0;

    private static final String tipos[] = {
            "Banqueta",
            "Estacionamiento",
            "Puertas",
            "Escalera",
            "Elevador",
            "Barandal",
            "Piso",
            "Recepción",
            "Comedor",
            "Cocina",
            "Manijas",
            "Sanitario Común",
            "Sanitario Completo",
            "Excusado",
            "Lavamanos",
            "Barras de Apoyo"
    };
    private static final String schema = "{  \"Banqueta\": {    \"Banqueta\": [      \"Cambio de textura o tira táctil en cruces\",      \"Separación de rejillas y entre calles de 13 mm con dirección diagonal o perpendicular\",      \"Espacio libre de obstáculos de 220x120 cm\",      \"Banqueta en buen estado\"    ],    \"Bordes Limitantes\" : [      \"Con color contrastante\",      \"Medidas de 5x10 cm\"    ]  },  \"Estacionamiento\": {},  \"Puertas\": {},  \"Escalera\": {},  \"Elevador\": {},  \"Barandal\": {},  \"Piso\": {},  \"Recepción\": {},  \"Comedor\": {},  \"Cocina\": {},  \"Manijas\": {},  \"Sanitario Común\": {},  \"Sanitario Completo\": {},  \"Excusado\": {},  \"Lavamanos\": {},  \"Barras de Apoyo\": {} }";

    private int action;
    private Map<String, Map<String, QuestionGroup>> evaluationQuestions = new LinkedHashMap<>();
    private QuestionGroup qGroups[];
    private Spinner spinner;
    private EditText evaluationName;
    private EditText frequencyET;
    private ProgressBar progressBarLoad;
    private Button editPictureButton;
    private String encoded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        progressBarLoad = (ProgressBar) findViewById(R.id.evaluationProgressBar);
        progressBarLoad.setVisibility(View.VISIBLE);
        editPictureButton = (Button)findViewById(R.id.evaluationPictureB);

        action = getIntent().getIntExtra("action", VIEW);

        JSONObject schemaJson = new JSONObject();
        try {
            schemaJson = new JSONObject(schema);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator<String> types = schemaJson.keys();
        while(types.hasNext()) {
            String type = types.next();
            Map<String, QuestionGroup> questionGroups = new LinkedHashMap<>();
            try {
                JSONObject jsonQuestionGroup = schemaJson.getJSONObject(type);
                Iterator<String> subTypes = jsonQuestionGroup.keys();
                while(subTypes.hasNext()) {
                    String subType = subTypes.next();

                    JSONArray jsonQuestions = jsonQuestionGroup.getJSONArray(subType);
                    List<Question> questions = new ArrayList<>();
                    for(int i = 0; i < jsonQuestions.length(); i++) {
                        questions.add(new Question(jsonQuestions.getString(i), "", Question.NA));
                    }
                    questionGroups.put(subType, new QuestionGroup(questions, subType));
                }
                evaluationQuestions.put(type, questionGroups);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        progressBarLoad.setVisibility(View.GONE);

        spinner = (Spinner) findViewById(R.id.evaluationSpinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipos));
        spinner.setEnabled(action != VIEW);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fillListView(tipos[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        evaluationName = (EditText) findViewById(R.id.evaluationET);
        evaluationName.setText("");
        evaluationName.setEnabled(action != VIEW);

        frequencyET = (EditText) findViewById(R.id.evaluationFrequencyET);
        frequencyET.setText("");
        frequencyET.setEnabled(action != VIEW);

        final String evaluationsUrl = getIntent().getStringExtra("evaluationUrl");
        if (action != CREATE) {
            Api.get(evaluationsUrl, new AbstractContinuation<JSONObject>() {

                @Override
                public void then(JSONObject data) {
                    try {
                        String name = data.getString("name");
                        int type = data.getInt("type");
                        int frequency = data.getInt("frequency");
                        JSONObject results = data.getJSONObject("data");
                        byte[] decodedString = Base64.decode(data.getString("picture"), Base64.DEFAULT);
                        Bitmap bitmapPicture = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        ((ImageView) findViewById(R.id.evaluationIV)).setImageBitmap(bitmapPicture);

                        Iterator<String> subtypes = results.keys();
                        while (subtypes.hasNext()) {
                            String subtype = subtypes.next();
                            JSONArray answers = results.getJSONArray(subtype);
                            QuestionGroup qGroup = evaluationQuestions.get(tipos[type]).get(subtype);
                            for (int i = 0; i < answers.length(); i++) {
                                JSONObject answer = answers.getJSONObject(i);
                                qGroup.getQuestions().get(i).setAnswer(answer.getInt("answer"));
                                qGroup.getQuestions().get(i).setMeasurements(answer.getString("measurements"));
                            }
                        }
                        fillListView(tipos[spinner.getSelectedItemPosition()]);
                        evaluationName.setText(name);
                        frequencyET.setText(Integer.toString(frequency));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void fail(Exception e) {
                    Toast.makeText(getApplicationContext(), "Ocurrió un error al contactar al servidor.", Toast.LENGTH_LONG).show();
                }
            });
        }
        findViewById(R.id.evaluationPictureB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        fillListView(tipos[spinner.getSelectedItemPosition()]);

        Button button = (Button) findViewById(R.id.evaluationB);
        if (action == VIEW) {
            button.setVisibility(View.GONE);
        } else if (action == EDIT) {
            button.setText("Editar");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = evaluationName.getText().toString();
                String frequencyStr = frequencyET.getText().toString();
                if (name.length() < 4 || name.length() > 70) {
                    Toast.makeText(getApplicationContext(), "El nombre debe tener entre 4 y 70 caracteres.", Toast.LENGTH_LONG).show();
                    return;
                }

                int frequency = 1;
                try {
                    frequency = Integer.parseInt(frequencyStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Frecuencia inválida.", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject results = new JSONObject();
                JSONObject reqBody = new JSONObject();
                for (QuestionGroup qGroup : qGroups) {
                    try {
                        JSONArray answers = new JSONArray();
                        for (Question question : qGroup.getQuestions()) {
                            JSONObject answer = new JSONObject();
                            answer.put("answer", question.getAnswer());
                            answer.put("measurements", question.getMeasurements());
                            answers.put(answer);
                        }
                        results.put(qGroup.getName(), answers);
                        reqBody.put("result", results);
                        reqBody.put("name", name);
                        reqBody.put("type", spinner.getSelectedItemPosition());
                        reqBody.put("frequency", frequency);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (encoded != null && !encoded.isEmpty()) {
                    try {
                        reqBody.put("picture", encoded);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (action == CREATE) {
                    Api.post(evaluationsUrl, reqBody, new AbstractContinuation<JSONObject>() {
                        @Override
                        public void then(JSONObject data) {
                            setResult(Activity.RESULT_OK, new Intent());
                            finish();
                        }

                        @Override
                        public void fail(Exception e) {
                            Toast.makeText(getApplicationContext(), "Ocurrió un error al contactar al servidor.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void fillListView(String type) {
        qGroups = evaluationQuestions.get(type).values().toArray(new QuestionGroup[]{});
        ((ListView) findViewById(R.id.evaluationQuestionsLV)).setAdapter(new QuestionGroupListAdapter(getApplicationContext(), R.layout.layout_question_group, new ArrayList<QuestionGroup>(Arrays.asList(qGroups)), action != VIEW));
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        if (req == REQUEST_IMAGE_CAPTURE) {
            if (res == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap image = (Bitmap) extras.get("data");
                ((ImageView) findViewById(R.id.evaluationIV)).setImageBitmap(image);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
        MenuItem text = menu.findItem(R.id.menuEvaluationUsername);
        text.setTitle(LoginActivity.getCurrentUser().getUsername());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menuEvaluationLogout:
                LoginActivity.logout(EvaluationActivity.this);
                return true;
            case R.id.menuEvaluationUsername:
                Intent intent = new Intent(EvaluationActivity.this, UserProfileActivity.class);
                intent.putExtra("collaboratorUrl", LoginActivity.getCurrentUser().getUrl());
                EvaluationActivity.this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
