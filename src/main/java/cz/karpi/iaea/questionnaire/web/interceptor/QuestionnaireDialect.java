package cz.karpi.iaea.questionnaire.web.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.dialect.IExpressionEnhancingDialect;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by karpi on 9.5.17.
 */
@Component
public class QuestionnaireDialect extends AbstractDialect implements IExpressionEnhancingDialect {

    private final QuestionnaireDialectUtils questionnaireDialectUtils;

    @Autowired
    public QuestionnaireDialect(QuestionnaireDialectUtils questionnaireDialectUtils) {
        super();
        this.questionnaireDialectUtils = questionnaireDialectUtils;
    }

    @Override
    public String getPrefix() {
        return "questionnaire";
    }

    @Override
    public Map<String, Object> getAdditionalExpressionObjects(IProcessingContext ctx) {
        Map<String, Object> expressions = new HashMap<>();
        expressions.put("questionnaireUtils", questionnaireDialectUtils);
        return expressions;
    }
}
