package cz.karpi.iaea.questionnaire.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import cz.karpi.iaea.questionnaire.service.QuestionnaireFacadeService;
import cz.karpi.iaea.questionnaire.service.exception.ValidationException;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.web.converter.ViewConverter;
import cz.karpi.iaea.questionnaire.web.model.AnswersVo;
import cz.karpi.iaea.questionnaire.web.model.InitVo;

/**
 * Created by karpi on 12.4.17.
 */
@Controller
public class WelcomeController {

    private static final String MODEL_ATTRIBUTE_COMMON = "common";
    private static final String MODEL_ATTRIBUTE_INIT = "init";
    private static final String MODEL_ATTRIBUTE_ANSWERS = "answers";
    private static final String MODEL_ATTRIBUTE_QUESTION = "questions";

    private final QuestionnaireFacadeService questionnaireFacadeService;

    private final ViewConverter viewConverter;

    /*todo pravo na stranku podle stavu flow*/
    @Autowired
    public WelcomeController(QuestionnaireFacadeService questionnaireFacadeService, ViewConverter viewConverter) {
        this.questionnaireFacadeService = questionnaireFacadeService;
        this.viewConverter = viewConverter;
    }

    @RequestMapping("/")
    public String index(Model model) throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        model.addAttribute(MODEL_ATTRIBUTE_INIT, new InitVo());
        return commonTo.getState().toLowerCase();
    }

    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public String initQuestionnaire(@ModelAttribute(MODEL_ATTRIBUTE_INIT) InitVo initVo, BindingResult errors, Model model) throws Exception {
        try {
            questionnaireFacadeService.init(viewConverter.toInitTo(initVo));
        } catch (ValidationException e) {
            errors.addError(new FieldError(MODEL_ATTRIBUTE_INIT, "companyName", ""));
        }
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        if (errors.hasErrors()) {
            return commonTo.getState().toLowerCase();
        }
        return "redirect:/" + commonTo.getState().toLowerCase();
    }

    @RequestMapping(value = "/instruction", method = RequestMethod.GET)
    public String instructionGet(Model model) throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        return commonTo.getState().toLowerCase();
    }

    @RequestMapping(value = "/instruction", method = RequestMethod.POST)
    public String instructionPost(Model model, @RequestParam String action) throws Exception {
        questionnaireFacadeService.instruction(viewConverter.convertToEAction(action));
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        return "redirect:/" + commonTo.getState().toLowerCase();
    }

    @RequestMapping(value = "/question", method = RequestMethod.GET)
    public String questionGet(Model model) throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        model.addAttribute(MODEL_ATTRIBUTE_QUESTION, viewConverter.toQuestionsVo(questionnaireFacadeService.getQuestionsTo()));
        model.addAttribute(MODEL_ATTRIBUTE_ANSWERS, viewConverter.toAnswersVo(questionnaireFacadeService.getAnswersTo()));
        return commonTo.getState().toLowerCase();
    }

    @RequestMapping(value = "/question", method = RequestMethod.POST)
    public String questionPost(@ModelAttribute(MODEL_ATTRIBUTE_ANSWERS) AnswersVo answerVo, @RequestParam String action, BindingResult errors, Model model) throws Exception {
        try {
            questionnaireFacadeService.question(viewConverter.toAnswersTo(answerVo), viewConverter.convertToEAction(action));
        } catch (ValidationException e) {
            ((List<Object[]>) e.getErrors()).forEach(objs -> {
                errors.addError(new FieldError(MODEL_ATTRIBUTE_ANSWERS, "answerList[" + objs[0] + "]." + objs[1], "Cannot be empty"));
            });
        }
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        model.addAttribute(MODEL_ATTRIBUTE_QUESTION, viewConverter.toQuestionsVo(questionnaireFacadeService.getQuestionsTo()));
        if (errors.hasErrors()) {
            return null;
        }
        model.addAttribute(MODEL_ATTRIBUTE_ANSWERS, viewConverter.toAnswersVo(questionnaireFacadeService.getAnswersTo()));
        return "redirect:/" + commonTo.getState().toLowerCase();
    }

    @RequestMapping(value = "/finish", method = RequestMethod.GET)
    public String finishGet(Model model) throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        return commonTo.getState().toLowerCase();
    }

}
