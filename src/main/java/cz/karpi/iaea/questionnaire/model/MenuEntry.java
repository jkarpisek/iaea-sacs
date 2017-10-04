package cz.karpi.iaea.questionnaire.model;

/**
 * Created by Myspulin on 18. 9. 2017.
 */
public class MenuEntry {
    String category;
    String subCategory;
    boolean active;

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "MenuEntry{" +
                "category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", active=" + active +
                '}';
    }
}
