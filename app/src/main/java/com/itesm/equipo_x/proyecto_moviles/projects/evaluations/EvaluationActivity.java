package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.common.AbstractContinuation;
import com.itesm.equipo_x.proyecto_moviles.common.Http.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class EvaluationActivity extends AppCompatActivity {

    private static final int VIEW = 0, EDIT = 1, CREATE = 2;
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
    private static final String schema = "  \"Banqueta\": {    \"Banqueta\": [      \"Cambio de textura o tira táctil en cruces\",      \"Separación de rejillas y entre calles de 13 mm con dirección diagonal o perpendicular\",      \"Espacio libre de obstáculos de 220x120 cm\",      \"Banqueta en buen estado\"    ],    \"Bordes Limitantes\" : [      \"Con color contrastante\",      \"Medidas de 5x10 cm\"    ]  },  \"Estacionamiento\": {},  \"Puertas\": {},  \"Escalera\": {},  \"Elevador\": {},  \"Barandal\": {},  \"Piso\": {},  \"Recepción\": {},  \"Comedor\": {},  \"Cocina\": {},  \"Manijas\": {},  \"Sanitario Común\": {},  \"Sanitario Completo\": {},  \"Excusado\": {},  \"Lavamanos\": {},  \"Barras de Apoyo\": {}}";

    private int action;
    private Evaluation evaluation;
    private Map<String, Map<String, QuestionGroup>> evaluationQuestions = evaluationQuestions = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

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
            Map<String, QuestionGroup> questionGroups = new HashMap<>();
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

        Spinner spinner = (Spinner) findViewById(R.id.evaluationSpinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipos));
        spinner.setEnabled(action != VIEW);

        if (action != CREATE) {
            final String evaluationsUrl = getIntent().getStringExtra("evaluationsUrl");
            Api.get(evaluationsUrl, new AbstractContinuation<JSONObject>() {

                @Override
                public void then(JSONObject data) {
                    try {
                        String name = data.getString("name");
                        int type = data.getInt("type");
                        int frequency = data.getInt("frequency");
                        JSONObject results = data.getJSONObject("data");
                        evaluation = new Evaluation(name, type, evaluationsUrl, frequency, data);

                        Iterator<String> subtypes = results.keys();
                        while (subtypes.hasNext()) {
                            String subtype = subtypes.next();
                            JSONArray answers = results.getJSONArray("subtype");
                            QuestionGroup qGroup = evaluationQuestions.get(tipos[type]).get(subtype);
                            for (int i = 0; i < answers.length(); i++) {
                                qGroup.getQuestions().get(i).setAnswer(answers.getInt(i));
                            }
                        }
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

        if (spinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {
            fillListView(tipos[spinner.getSelectedItemPosition()]);
        }

        ((EditText) findViewById(R.id.evaluationET)).setEnabled(action != VIEW);

        Button button = (Button) findViewById(R.id.evaluationB);
        if (action == VIEW) {
            button.setVisibility(View.GONE);
        } else if (action == EDIT) {
            button.setText("Editar");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void fillListView(String type) {
        QuestionGroup[] questionGroups = evaluationQuestions.get("type").values().toArray(new QuestionGroup[]{});
        ((ListView) findViewById(R.id.evaluationQuestionsLV)).setAdapter(new QuestionGroupListAdapter(getApplicationContext(), R.id.evaluationQuestionsLV, new ArrayList<QuestionGroup>(Arrays.asList(questionGroups))));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_evaluation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
