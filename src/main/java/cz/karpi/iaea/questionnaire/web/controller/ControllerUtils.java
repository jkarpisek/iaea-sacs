package cz.karpi.iaea.questionnaire.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.function.Supplier;

import cz.karpi.iaea.questionnaire.service.QuestionnaireFacadeService;
import cz.karpi.iaea.questionnaire.service.exception.ValidationException;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.web.converter.ViewConverter;

/**
 * Created by karpi on 3.5.17.
 */
@Component
public class ControllerUtils {

    private static final String PATTERN_PAGE = "%s";
    private static final String PATTERN_PAGE_REDIRECT = "redirect:/%s";

    private static final String MODEL_ATTRIBUTE_COMMON = "common";

    private final QuestionnaireFacadeService questionnaireFacadeService;

    private final ViewConverter viewConverter;

    @Autowired
    public ControllerUtils(QuestionnaireFacadeService questionnaireFacadeService, ViewConverter viewConverter) {
        this.questionnaireFacadeService = questionnaireFacadeService;
        this.viewConverter = viewConverter;
    }

    protected void catchValidationException(BindingResult errors, Supplier<Void> serviceCall) {
        try {
            serviceCall.get();
        } catch (ValidationException e) {
            //todo ATTRIBUTE, answerList, message
            ((List<Object[]>) e.getErrors()).forEach(objs -> {
                errors.addError(new FieldError("", "answerList[" + objs[0] + "]." + objs[1], "Cannot be empty"));
            });
        }
    }

    protected String returnGet(Model model) {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        return toPage(commonTo.getState(), false);
    }

    protected String returnPost(Model model, BindingResult errors) {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        if (errors != null && errors.hasErrors()) {
            model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
            return null;
        }
        return toPage(commonTo.getState(), true);
    }

    protected String toPage(String state, Boolean redirect) {
        return String.format(redirect ? PATTERN_PAGE_REDIRECT : PATTERN_PAGE, state.toLowerCase());
    }

}
