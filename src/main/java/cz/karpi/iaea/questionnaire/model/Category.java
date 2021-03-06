package cz.karpi.iaea.questionnaire.model;

import java.util.List;

/**
 * Created by karpi on 12.4.17.
 */
public class Category {
    private String name;
    private List<SubCategory> subCategories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    @Override
    public String toString() {
        return "Category{" +
               "name='" + name + '\'' +
               ", subCategories=" + subCategories +
               '}';
    }
}
