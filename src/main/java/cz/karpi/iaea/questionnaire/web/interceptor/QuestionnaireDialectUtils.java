package cz.karpi.iaea.questionnaire.web.interceptor;

import org.springframework.stereotype.Component;

import cz.karpi.iaea.questionnaire.web.model.MatrixMap;

/**
 * Created by karpi on 10.5.17.
 */
@Component
public class QuestionnaireDialectUtils {
    public String getPropertyName(String key1, String key2) {
        return MatrixMap.getPropertyName(key1, key2);
    }
}
