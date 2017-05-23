package cz.karpi.iaea.questionnaire.web.interceptor;

import org.springframework.stereotype.Component;

/**
 * Created by karpi on 10.5.17.
 */
@Component
public class QuestionnaireDialectUtils {
    public String getPropertyName(String key1, String key2) {
        return String.join(";", key1, key2);
    }
}
