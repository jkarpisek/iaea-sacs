package cz.karpi.iaea.questionnaire.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karpi on 2.5.17.
 */
public class AssessmentGridRow {
    private AbstractAnswerRow answerRow;
    private Map<Element, Integer> elementsPiGrade;

    public AbstractAnswerRow getAnswerRow() {
        return answerRow;
    }

    public void setAnswerRow(AbstractAnswerRow answerRow) {
        this.answerRow = answerRow;
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
}
