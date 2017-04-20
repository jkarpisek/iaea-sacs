package cz.karpi.iaea.questionnaire.web.model;

/**
 * Created by karpi on 15.4.17.
 */
public class QuestionVo {
    private String number;
    private String text;
    private String type;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
