package cz.karpi.iaea.questionnaire.service.to;

/**
 * Created by karpi on 15.4.17.
 */
public class PlannerOverviewTo extends PlannerTo {
    private PlannerAnswersTo value;

    public PlannerAnswersTo getValue() {
        return value;
    }

    public void setValue(PlannerAnswersTo value) {
        this.value = value;
    }
}
