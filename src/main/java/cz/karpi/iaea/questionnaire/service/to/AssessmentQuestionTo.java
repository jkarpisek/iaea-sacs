package cz.karpi.iaea.questionnaire.service.to;

/**
 * Created by karpi on 15.4.17.
 */
public class AssessmentQuestionTo extends QuestionTo {
    private String answer;
    private Integer piGrade;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getPiGrade() {
        return piGrade;
    }

    public void setPiGrade(Integer piGrade) {
        this.piGrade = piGrade;
    }
}
