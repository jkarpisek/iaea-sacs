package cz.karpi.iaea.questionnaire.service.to;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class QuestionsTo {
    private String category;
    private String subCategory;
    private List<QuestionTo> questionList;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public List<QuestionTo> getQuestionList() {
        if (questionList == null) {
            questionList = new ArrayList<>();
        }
        return questionList;
    }

    public void setQuestionList(List<QuestionTo> questionList) {
        this.questionList = questionList;
    }
}
