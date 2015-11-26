package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rrs94 on 19/11/15.
 */
public class QuestionGroup {

    private List<Question> questions = new ArrayList<>();
    private String name;

    public QuestionGroup(List<Question> questions, String name) {
        this.questions = questions;
        this.name = name;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
