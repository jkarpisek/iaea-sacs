package cz.karpi.iaea.questionnaire.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karpi on 2.5.17.
 */
public class AssessmentRow {
    private Question question;
    private Map<Element, Integer> elementsPiGrade;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Map<Element, Integer> getElementsPiGrade() {
        if (elementsPiGrade == null) {
            elementsPiGrade = new HashMap<>();
        }
        return elementsPiGrade;
    }

    public void setElementsPiGrade(Map<Element, Integer> elementsPiGrade) {
        this.elementsPiGrade = elementsPiGrade;
    }

    @Override
    public String toString() {
        return "AssessmentRow{" +
               "question=" + question +
               ", elementsPiGrade=" + elementsPiGrade +
               '}';
    }
}
