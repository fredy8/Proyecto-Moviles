package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.itesm.equipo_x.proyecto_moviles.R;

import java.util.List;

/**
 * Created by rrs94 on 18/11/15.
 */
public class EvaluationListAdapter extends ArrayAdapter<Evaluation> {
    private Context context;
    private int resource;
    private List<Evaluation> evaluations;
    private Activity activity;

    public EvaluationListAdapter(Context context, int resource, List<Evaluation> evaluations, Activity activity) {
        super(context, resource, evaluations);
        this.context = context;
        this.resource = resource;
        this.evaluations = evaluations;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View row = inflater.inflate(resource, parent, false);
        final Evaluation evaluation = evaluations.get(position);

        ((TextView)row.findViewById(R.id.projectNameTV)).setText(evaluations.get(position).getName());
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EvaluationActivity.class);
                intent.putExtra("evaluationUrl", evaluation.getEvaluationUrl());
                EvaluationListAdapter.this.activity.startActivity(intent);
            }
        });
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                row.showContextMenu();
                return true;
            }
        });
        return row;
    }

    public void addEvaluation(Evaluation evaluation) {
        this.evaluations.add(evaluation);
        this.notifyDataSetChanged();
    }
}
