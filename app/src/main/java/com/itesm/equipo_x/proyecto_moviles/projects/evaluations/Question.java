package com.itesm.equipo_x.proyecto_moviles.projects.evaluations;

/**
 * Created by rrs94 on 19/11/15.
 */
public class Question {

    public static final int YES = 0, NO = 1, NA = 2;

    private String question, measurements;
    private int answer = YES;

    public Question(String question, String measurements, int answer) {
        this.question = question;
        this.measurements = measurements;
        this.answer = answer;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getMeasurements() {
        return measurements;
    }

    public void setMeasurements(String measurements) {
        this.measurements = measurements;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
