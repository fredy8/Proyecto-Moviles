package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

import org.json.JSONObject;

/**
 * Created by rrs94 on 18/11/15.
 */
public class Evaluation {
    private String name, evaluationUrl;
    private JSONObject data;
    private int frequency, type;

    public Evaluation(String name, int type, String url) {
        this.name = name;
        this.type = type;
        this.evaluationUrl = url;
    }

    public Evaluation(String name, int type, String url, int frequency, JSONObject data) {
        this(name, type, url);
        this.frequency = frequency;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEvaluationUrl() {
        return evaluationUrl;
    }

    public void setEvaluationUrl(String evaluationUrl) {
        this.evaluationUrl = evaluationUrl;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
