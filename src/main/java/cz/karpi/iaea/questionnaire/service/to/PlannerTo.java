package cz.karpi.iaea.questionnaire.service.to;

import java.util.List;

import cz.karpi.iaea.questionnaire.model.Year;

/**
 * Created by karpi on 15.4.17.
 */
public class PlannerTo extends AssessmentTo {
    private List<Year> years;
    private List<PlannerQuestionTo> plannerQuestions;

    public List<Year> getYears() {
        return years;
    }

    public void setYears(List<Year> years) {
        this.years = years;
    }

    public List<PlannerQuestionTo> getPlannerQuestions() {
        return plannerQuestions;
    }

    public void setPlannerQuestions(List<PlannerQuestionTo> plannerQuestions) {
        this.plannerQuestions = plannerQuestions;
    }
}
