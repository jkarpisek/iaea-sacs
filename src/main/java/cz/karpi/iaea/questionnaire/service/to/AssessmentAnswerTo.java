package cz.karpi.iaea.questionnaire.service.to;

import java.util.Map;

import cz.karpi.iaea.questionnaire.model.Element;

/**
 * Created by karpi on 15.4.17.
 */
public class AssessmentAnswerTo {
    private String number;
    private Map<Element, Integer> piGrades;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Map<Element, Integer> getPiGrades() {
        return piGrades;
    }

    public void setPiGrades(Map<Element, Integer> piGrades) {
        this.piGrades = piGrades;
    }
}
