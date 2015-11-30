package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
            "Rampa para banqueta",
            "Pasillo",
            "Rampa",
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
    private static final String schema = "{  \"Banqueta\": {    \"Banqueta\": [      \"Cambio de textura o tira táctil en cruces\",      \"Separación de rejillas y entre calles de 13 mm con dirección diagonal o perpendicular\",      \"Espacio libre de obstáculos de 220x120 cm\",      \"Banqueta en buen estado\"    ],    \"Bordes Limitantes\" : [      \"Con color contrastante\",      \"Medidas de 5x10 cm\"    ]  },  \"Estacionamiento\": {    \"Banqueta\": [      \"Cambio de textura o tira táctil en cruces.\",      \"Separación de rejillas y entre calles de 13 mm con dirección diagonal o perpendicular.\",      \"Espacio libre de obstáculos de 220x120 cm.\",      \"Banqueta en buen estado\",      \"Con color contrastante\"    ],    \"Perpendicular o diagonal a la banqueta\":[      \"*Cajón de 380x500 cm sin circulación central.\",      \"*Cajón de 250x500 cm con circulación central.\",      \"Circulación central de 150x150 cm.\"    ],    \"Señalamientos\" : [      \"Con SIA a una altura de 170 cm.\",      \"Dimensione mínimas del señalamiento de 30x45 cm.\",      \"Con SIA sobre el piso de minimo de 160x144 cm.\",      \"Color contrastante\"    ],    \"Cajones\" : [      \"Área de circulación marcada con color contrastante\",      \"Tope de rueda que evita que los autos obstruyan la banqueta\",      \"Cambio de nivel con rampa adecuada.\",      \"El 4% del total de los cajoens son exclusicos para PCD (o al menos 1 si el porcentaje no se alcanza).\"    ]    },  \"Puertas\": {    \"Entradas principales\" : [        \" Con una  pendiente el 2% en el umbral de la puerta.\",        \"Se indica la ruta para llegar a la puerta accesible en caso de que la entrada principal no lo sea.\",        \"Acceso cubierto\",        \"Con un espacio de maniobra de 150 cm en el interior de la puerta.\"    ],    \"Puertas interiores\" : [        \"Puertas y marcos con color contrastante en relación a los muros.\",        \"Puertas de cristal marcadas con algún señalamiento contrastante.\",        \"Puertas mecánicas con un tiempo de apertura de 5 segundos.\",        \"Dimensiones de 210x100 cm.\",        \"Elementos de señalización o cambios de textura en el piso.\",        \"Zoclo de protección con una altura de 20.5 cm.\",        \"Evita estar enseguida de una rampa o escalera.\"    ],    \"Manija\" :[        \"Manija con protuberancia.\",        \"Altura de manija de 95 cm.\",        \"Altura de cerradura de 95 cm.\",        \"Separación entre puerta y manija de 5 a 7 cm.\",        \"Se evitan manijas giratorias.\"    ]    },  \"Escalera\": {    \"Escalera\" : [        \"*Superficie del piso antiderrapante.\",        \"*Tira antiderrapante a 2.5 cm de la nariz del escalón.\",        \"Sin encharcamientos y/o alcantarillados.\",        \"Pendiente máxima del 2%\",        \"Ancho mínimo de 120 cm.\",        \"Peralte de color contrastante a la huella.\"    ],    \"Escalón\" :[        \"Huella mínimo de 28 cm.\",        \"Peralte máximo de 18 cm.\",        \"Nariz con remate boleado u ochavado con 3.5 cm de radio máximo.\"    ],    \"Protección\" : [        \"Barrera o señalización de mínimo 5x5 cm bajo la proyección de la rampa que salve 210 cm de altura.\"        ],    \"Descanso\" : [        \"Cada 12 escalones\",        \"Longitud minima del ancho de la escalera\"    ],    \"Zona de aproximación\" :[        \"Sin encharcamientos y/o alcantarillados.\",        \"*Tira táctil con dimensiones de 30 cm por el ancho de la rampa.\",        \"*Texturizado con área de 120 cm por el ancho de la escalera.\",        \"Color contrastante.\",        \"Área libre de obstáculos con dimensiones mínimas de 120x150 cm.\"    ],    \"Pasamanos\" :[        \"Cumple con las especificaciones.\",        \"Pasamanos de ambos lados.\",        \"Sobre sale 30 cm de la rampa, sobre la zonda de aproximación, al incio y al final.\"    ]    },  \"Elevador\": {    \"Acceso\" : [        \"Ubicación cercana a la entrada principal.\",            \"Ruta accesible señalizada\"    ],    \"Exterior del Elevador\" : [        \"En braile  y alto relieve\",        \"Altura máxima de botones 120 cm.\",        \"Botones a 30 cm del vano.\",        \"Luminosa y sonora al llegar el elevador, apertura y cierre de puertas.\",        \"Marco del elevador de color contrastante.\"    ],    \"Interior del elevador\" : [        \"Tiempo de apertura de puertas 8 segundos.\",        \"Altura máxima de botones 120 cm.\",        \"Luminosa y sonora en cambios de piso.\",        \"Piso del elevador y piso exterior al mismo nivel.\",        \"Separación máxima entre el piso del elevador y el nivel del piso exterior de 2 cm.\",        \"Ojo eléctrico a 20 cm de altura.\",        \"Dimensiones de 150x170 cm.\",        \"Ancho mínimo libre de la puerta de 90 cm.\"    ],    \"Pasamanos\" : [        \"Cumple con las especificaciones.\",        \"A una altura de 90 y 75 cm.\",        \"En los tres lados de la cabina.\",        \"Separación de 5 cm de la pared.\"    ],    \"Zona de aproximación\" : [        \"Sin encharcamientos y/o alcantarillados.\",        \"*Tira táctil con dimensiones de 30 cm por el ancho del elevador.\",        \"*Texturizado con longitud de 30 cm por el ancho del elevador.\",        \"Color contrastante.\"    ]    },  \"Rampa para banqueta\" :{    \"Peralte\" : [        \"*Peralte mayor a 32 cm con pendiente del 6%\",        \"*Peralte menor a 16 cm con pendiente del 8%\"    ],    \"Rampa de Bordes laterales\" : [        \"Ancho mínimo 120 cm.\",        \"Espacio de maniobra sobre la banqueta de 140 cm de diámetro.\",        \"Pendiente lateral del 10%.\"    ],    \"Esquina en abanico\" : [        \"Ancho minimo 140 cm\",        \"Cuenta con minimo dos bolardos con una distancia entre ellos de 120 cm\"    ],    \"Zona de aproximación\" :[        \"Sin encharcamientos y/o alcantarillados.\",        \"*Tira táctil con dimensiones de 20 cm por el largo de la rampa.\",        \"*Texturizado con ancho de 20 cm por el largo de la rampa.\",        \"Superficie antiderrapante.\"    ]    },  \"Pasillo\": {    \"Pasillo\" : [        \"*Hacia recepción con ancho mínimo de 150 cm.\",        \"*Hacia accesos con ancho mínimo de 120 cm.\"    ],    \"objetos en fachada\" :[        \"Altura minima de 210 cm\",        \"No sobresale mas de 20 cm\"    ],    \"Objetos empotrados al muro\" : [        \"Altura minima de 68 cm\",        \"No sobresale mas de 10 cm\",        \"Borde de proteccion/zona de aproximación en la proyección del objeto\"    ],    \"Objetos de piso fijos\" :[        \"Altura maxima de 68 cm\",        \"Deja un espacio libre en el pasillo de 120 cm\"    ],    \"Objetos montados en postes\" : [        \"Altura minima de 68 cm\",        \"No sobresale mas de 40 cm\",        \"Borde de proteccion de 5x5 cm en la proyección del objetoo un cambio de textura en piso\"    ]    },  \"Rampa\": {    \"Rampa\":[        \"Superficie antiderrapante.\",        \"Separación de rejillas y entre calles de 13 mm con dirección diagonal o perpendicular.\"    ],    \"Protección\":[        \"Ancho mínimo de 120 cm.\",        \"Borde de protección de 5x5 cm.\",        \"Barrera o señalización de mínimo 5x5 cm bajo la proyección de la rampa que salve 210 cm de altura.\"    ],    \"Pasamanos\":[        \"Cumple con las especificaciones.\",        \"Sobre sale 30 cm de la rampa, sobre la zona de aproximación, al inicio y al final.\"    ],    \"Pendiente\":[        \"*Pendiente del 5 % en longitud máxima de 1000 cm.\",        \"*Pendiente del 6 % en longitud máxima de 600 cm.\",        \"*Pendiente del 8 % en longitud máxima de 300 cm.\",        \"*Pendiente del 10 % en longitud máxima de 150 cm.\"    ],    \"Zona de aproximación\":[        \"Sin encharcamientos y/o alcantarillados.\",        \"*Tira táctil con dimensiones de 30 cm por el ancho de la rampa.\",        \"*Texturizado con área de 120 cm por el ancho de la rampa.\",        \"Color contrastante.\"    ]    },  \"Recepción\": {},  \"Comedor\": {},  \"Cocina\": {},  \"Manijas\": {},  \"Sanitario Común\": {},  \"Sanitario Completo\": {},  \"Excusado\": {},  \"Lavamanos\": {},  \"Barras de Apoyo\": {}}";

    private int action;
    private Map<String, Map<String, QuestionGroup>> evaluationQuestions = new LinkedHashMap<>();
    private QuestionGroup qGroups[];
    private Spinner spinner;
    private EditText evaluationName;
    private EditText frequencyET;
    private ProgressBar progressBarLoad;
    private Button editPictureButton;
    private String encoded;
    private final List<Double> coordinates = new ArrayList<>();

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
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos));
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
//                        fillListView(tipos[spinner.getSelectedItemPosition()]);
                        spinner.setSelection(type);
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

        if (action == CREATE) {
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

                    if (coordinates.size() != 0) {
                        try {
                            reqBody.put("latitude", coordinates.get(0));
                            reqBody.put("longitude", coordinates.get(1));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

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
