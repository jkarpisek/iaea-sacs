package cz.karpi.iaea.questionnaire.service.to;

/**
 * Created by karpi on 15.4.17.
 */
public class AnswerTo {
    private String number;
    private Integer piGrade;
    private String comments;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getPiGrade() {
        return piGrade;
    }

    public void setPiGrade(Integer piGrade) {
        this.piGrade = piGrade;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
