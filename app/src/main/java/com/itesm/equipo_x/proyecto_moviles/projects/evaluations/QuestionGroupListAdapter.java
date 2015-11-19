package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.R;
import com.itesm.equipo_x.proyecto_moviles.profiles.User;
import com.itesm.equipo_x.proyecto_moviles.profiles.UserProfile;

import java.util.List;

/**
 * Created by rrs94 on 19/11/15.
 */
public class QuestionGroupListAdapter extends ArrayAdapter<QuestionGroup> {

    private Context context;
    private int resource;
    private List<QuestionGroup> questionGroups;

    public QuestionGroupListAdapter(Context context, int resource, List<QuestionGroup> questionGroups) {
        super(context, resource, questionGroups);
        this.context = context;
        this.resource = resource;
        this.questionGroups = questionGroups;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(resource, parent, false);
        final QuestionGroup questionGroup = questionGroups.get(position);

        ((ListView)row.findViewById(R.id.questionGroupQuestionsLV)).setAdapter(new QuestionListAdapter(context, R.layout.layout_question, questionGroup.getQuestions()));
        return row;
    }

}
