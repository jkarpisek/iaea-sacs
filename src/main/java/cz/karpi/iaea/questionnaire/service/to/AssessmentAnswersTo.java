package cz.karpi.iaea.questionnaire.service.to;

import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class AssessmentAnswersTo {
    private List<AssessmentAnswerTo> answerList;

    public List<AssessmentAnswerTo> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<AssessmentAnswerTo> answerList) {
        this.answerList = answerList;
    }
}
