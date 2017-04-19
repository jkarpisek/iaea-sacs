package cz.karpi.iaea.questionnaire.model;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by karpi on 12.4.17.
 */
public class SubCategory {
    private Category category;
    private String name;
    private List<AbstractAnswerRow> answerRows;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AbstractAnswerRow> getAnswerRows() {
        if (answerRows == null) {
            answerRows = new ArrayList<>();
        }
        return answerRows;
    }

    public void setAnswerRows(List<AbstractAnswerRow> answerRows) {
        this.answerRows = answerRows;
    }
}
