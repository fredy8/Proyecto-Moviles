package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.R;

import java.util.List;

/**
 * Created by rrs94 on 19/11/15.
 */
public class QuestionGroupListAdapter extends ArrayAdapter<QuestionGroup> {

    private Context context;
    private int resource;
    private List<QuestionGroup> questionGroups;
    private boolean editable;

    public QuestionGroupListAdapter(Context context, int resource, List<QuestionGroup> questionGroups, boolean editable) {
        super(context, resource, questionGroups);
        this.context = context;
        this.resource = resource;
        this.questionGroups = questionGroups;
        this.editable = editable;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout row = (LinearLayout)inflater.inflate(resource, parent, false);
        final QuestionGroup questionGroup = questionGroups.get(position);

        ((TextView) row.findViewById(R.id.questionGroupTitleTV)).setText(questionGroup.getName());

        for(int i = 0; i < questionGroup.getQuestions().size(); i++) {
            row.addView(getView(questionGroup.getQuestions(), i, parent));
        }

        return row;
    }

    private View getView(List<Question> questions, int position, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.layout_question, parent, false);

        final Question question = questions.get(position);

        TextView questionTV = (TextView) row.findViewById(R.id.questionQuestionTV);
        final EditText measurementsET = (EditText) row.findViewById(R.id.questionMeasurementsET);
        RadioButton yesRB = (RadioButton) row.findViewById(R.id.questionYesRB);
        RadioButton noRB = (RadioButton) row.findViewById(R.id.questionNoRB);
        RadioButton naRB = (RadioButton) row.findViewById(R.id.questionNARB);
        RadioGroup radioGroup = (RadioGroup) row.findViewById(R.id.questionRG);

        questionTV.setText(question.getQuestion());
        switch(question.getAnswer()) {
            case Question.YES:
                radioGroup.check(R.id.questionYesRB);
                break;
            case Question.NO:
                radioGroup.check(R.id.questionNoRB);
                break;
            case Question.NA:
                radioGroup.check(R.id.questionNARB);
                break;
        }
        measurementsET.setText(question.getMeasurements());

        if (!editable) {
            yesRB.setClickable(false);
            noRB.setClickable(false);
            naRB.setClickable(false);
            measurementsET.setEnabled(false);
        } else {
            yesRB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    question.setAnswer(Question.YES);
                }
            });
            noRB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    question.setAnswer(Question.NO);
                }
            });
            naRB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    question.setAnswer(Question.NA);
                }
            });
            measurementsET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    question.setMeasurements(measurementsET.getText().toString());
                }
            });

        }

        return row;
    }

}
