package cz.karpi.iaea.questionnaire.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karpi on 16.5.17.
 */
public class Year {
    private Integer name;
    private List<Quarter> quarters;

    public Year(Integer name) {
        this.name = name;
    }

    public Integer getName() {
        return name;
    }

    public List<Quarter> getQuarters() {
        if (quarters == null) {
            quarters = new ArrayList<>();
        }
        return quarters;
    }

    public void setQuarters(List<Quarter> quarters) {
        this.quarters = quarters;
    }
}
