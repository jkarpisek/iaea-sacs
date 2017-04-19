package cz.karpi.iaea.questionnaire.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karpi on 12.4.17.
 */
public class SACSForm {
    List<Category> categories;

    public List<Category> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
