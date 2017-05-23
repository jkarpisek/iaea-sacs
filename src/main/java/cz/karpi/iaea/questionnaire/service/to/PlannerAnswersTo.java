package cz.karpi.iaea.questionnaire.service.to;

import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class PlannerAnswersTo {
    private List<PlannerAnswerTo> answerList;

    public List<PlannerAnswerTo> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<PlannerAnswerTo> answerList) {
        this.answerList = answerList;
    }
}
