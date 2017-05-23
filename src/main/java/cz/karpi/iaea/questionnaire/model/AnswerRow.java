package cz.karpi.iaea.questionnaire.model;

/**
 * Created by karpi on 12.4.17.
 */
public class AnswerRow {
    private Question question;
    private String comments;
    private Integer piGrade;

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getPiGrade() {
        return piGrade;
    }

    public void setPiGrade(Integer piGrade) {
        this.piGrade = piGrade;
    }
}
