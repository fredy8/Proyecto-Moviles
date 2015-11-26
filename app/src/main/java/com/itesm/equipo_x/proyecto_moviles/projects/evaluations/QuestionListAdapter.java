package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.R;

import java.util.List;

/**
 * Created by rrs94 on 19/11/15.
 */
public class QuestionListAdapter extends ArrayAdapter<Question> {

    private Context context;
    private int resource;
    private List<Question> questions;

    public QuestionListAdapter(Context context, int resource, List<Question> questions) {
        super(context, resource, questions);
        this.context = context;
        this.resource = resource;
        this.questions = questions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(resource, parent, false);

        Question question = questions.get(position);

        ((TextView) row.findViewById(R.id.questionQuestionTV)).setText(question.getQuestion());
        switch(question.getAnswer()) {
            case Question.YES:
                ((RadioButton) row.findViewById(R.id.questionYesRB)).setSelected(true);
                break;
            case Question.NO:
                ((RadioButton) row.findViewById(R.id.questionNoRB)).setSelected(true);
                break;
            case Question.NA:
                ((RadioButton) row.findViewById(R.id.questionNARB)).setSelected(true);
                break;
        }
        ((EditText) row.findViewById(R.id.questionMeasurementsET)).setText(question.getMeasurements());

        return row;
    }
}
