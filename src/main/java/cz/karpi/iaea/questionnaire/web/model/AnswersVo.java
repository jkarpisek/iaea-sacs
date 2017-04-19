package cz.karpi.iaea.questionnaire.web.model;

import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class AnswersVo {
    private List<AnswerVo> answerList;

    public List<AnswerVo> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<AnswerVo> answerList) {
        this.answerList = answerList;
    }
}
