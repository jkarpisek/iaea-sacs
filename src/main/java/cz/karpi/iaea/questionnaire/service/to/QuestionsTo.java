package cz.karpi.iaea.questionnaire.service.to;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class QuestionsTo extends AbstractTo {
    private List<QuestionTo> questions;

    public List<QuestionTo> getQuestions() {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        return questions;
    }

    public void setQuestions(List<QuestionTo> questions) {
        this.questions = questions;
    }
}
