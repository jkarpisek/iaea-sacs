package cz.karpi.iaea.questionnaire.service.to;

import java.util.Map;

/**
 * Created by karpi on 15.4.17.
 */
public class PlannerQuestionTo extends AssessmentQuestionTo {
    private Map<String, Integer> piGrades;

    public Map<String, Integer> getPiGrades() {
        return piGrades;
    }

    public void setPiGrades(Map<String, Integer> piGrades) {
        this.piGrades = piGrades;
    }
}
