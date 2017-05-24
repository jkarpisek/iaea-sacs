package cz.karpi.iaea.questionnaire.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karpi on 16.5.17.
 */
public class PlannerRow {
    private Question question;
    private Element element;
    private Map<Quarter, Boolean> planned;
    private String task;
    private String ownership;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Map<Quarter, Boolean> getPlanned() {
        if (planned == null) {
            planned = new HashMap<>();
        }
        return planned;
    }

    public void setPlanned(Map<Quarter, Boolean> planned) {
        this.planned = planned;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    @Override
    public String toString() {
        return "PlannerRow{" +
               "question=" + question +
               ", element=" + element +
               ", planned=" + planned +
               ", task='" + task + '\'' +
               ", ownership='" + ownership + '\'' +
               '}';
    }
}
