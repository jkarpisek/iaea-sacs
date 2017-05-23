package cz.karpi.iaea.questionnaire.web.model;

import java.util.Map;

public class MatrixModel {

    private MatrixMap value = new MatrixMap();

    public MatrixMap getValue() {
        return value;
    }

    public void setValue(MatrixMap value) {
        this.value = value;
    }

    public Map<String, Map<String, Object>> getValues() {
        return getValue().getValues();
    }
}
