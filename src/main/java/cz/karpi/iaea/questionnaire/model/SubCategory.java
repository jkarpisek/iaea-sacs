package cz.karpi.iaea.questionnaire.model;

import java.util.List;

/**
 * Created by karpi on 12.4.17.
 */
public class SubCategory {
    private Category category;
    private String name;
    private List<Question> questions;

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

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    @Override
    public String toString() {
        return "SubCategory{" +
               "category=" + category +
               ", name='" + name + '\'' +
               ", questions=" + questions +
               '}';
    }
}
