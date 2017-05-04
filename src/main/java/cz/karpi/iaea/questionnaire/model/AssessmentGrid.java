package cz.karpi.iaea.questionnaire.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karpi on 2.5.17.
 */
public class AssessmentGrid {
    private List<AssessmentGridRow> rows;

    public List<AssessmentGridRow> getRows() {
        if (rows == null) {
            rows = new ArrayList<>();
        }
        return rows;
    }

    public void setRows(List<AssessmentGridRow> rows) {
        this.rows = rows;
    }
}
