package cz.karpi.iaea.questionnaire.web.model;

import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class QuestionsVo {
    private String category;
    private String subCategory;
    private List<QuestionVo> questionList;

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

    public List<QuestionVo> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuestionVo> questionList) {
        this.questionList = questionList;
    }
}
