package cz.karpi.iaea.questionnaire.service.to;

import java.util.Map;

/**
 * Created by karpi on 15.4.17.
 */
public class PlannerQuestionTo extends AssessmentQuestionTo {
    private Map<String, Integer> piGrade;

    public Map<String, Integer> getPiGrade() {
        return piGrade;
    }

    public void setPiGrade(Map<String, Integer> piGrade) {
        this.piGrade = piGrade;
    }
}
