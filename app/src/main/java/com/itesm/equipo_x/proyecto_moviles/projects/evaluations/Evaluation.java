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
