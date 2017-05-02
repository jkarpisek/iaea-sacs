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

import cz.karpi.iaea.questionnaire.model.Flow;
import cz.karpi.iaea.questionnaire.service.QuestionnaireFacadeService;
import cz.karpi.iaea.questionnaire.service.exception.ValidationException;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.web.converter.ViewConverter;
import cz.karpi.iaea.questionnaire.web.interceptor.FlowInterceptor;
import cz.karpi.iaea.questionnaire.web.model.AnswersVo;
import cz.karpi.iaea.questionnaire.web.model.InitVo;

/**
 * Created by karpi on 12.4.17.
 */
@Controller
@RequestMapping(value = "/")
public class WelcomeController {

    private static final String MODEL_ATTRIBUTE_COMMON = "common";
    private static final String MODEL_ATTRIBUTE_INIT = "init";
    private static final String MODEL_ATTRIBUTE_ANSWERS = "answers";
    private static final String MODEL_ATTRIBUTE_QUESTION = "questions";

    private final QuestionnaireFacadeService questionnaireFacadeService;

    private final ViewConverter viewConverter;

    @Autowired
    public WelcomeController(QuestionnaireFacadeService questionnaireFacadeService, ViewConverter viewConverter) {
        this.questionnaireFacadeService = questionnaireFacadeService;
        this.viewConverter = viewConverter;
    }

    @FlowInterceptor.FlowCheck({Flow.EFlowType.START, Flow.EFlowType.INSTRUCTION, Flow.EFlowType.QUESTION, Flow.EFlowType.FINISH})
    @RequestMapping
    public String index() throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        return viewConverter.toPage(commonTo.getState(), true);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.START)
    @RequestMapping("start")
    public String start(Model model) throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        model.addAttribute(MODEL_ATTRIBUTE_INIT, new InitVo());
        return viewConverter.toPage(commonTo.getState(), false);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.START)
    @RequestMapping(value = "start", method = RequestMethod.POST)
    public String start(@ModelAttribute(MODEL_ATTRIBUTE_INIT) InitVo initVo, BindingResult errors, Model model) throws Exception {
        try {
            questionnaireFacadeService.init(viewConverter.toInitTo(initVo));
        } catch (ValidationException e) {
            errors.addError(new FieldError(MODEL_ATTRIBUTE_INIT, "companyName", ""));
        }
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        if (errors.hasErrors()) {
            return null;
        }
        return viewConverter.toPage(commonTo.getState(), true);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.INSTRUCTION)
    @RequestMapping(value = "instruction")
    public String instruction(Model model) throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        return viewConverter.toPage(commonTo.getState(), false);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.INSTRUCTION)
    @RequestMapping(value = "instruction", method = RequestMethod.POST)
    public String instruction(Model model, @RequestParam String action) throws Exception {
        questionnaireFacadeService.instruction(viewConverter.convertToEAction(action));
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        return viewConverter.toPage(commonTo.getState(), true);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.QUESTION)
    @RequestMapping(value = "question")
    public String question  (Model model) throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        model.addAttribute(MODEL_ATTRIBUTE_QUESTION, viewConverter.toQuestionsVo(questionnaireFacadeService.getQuestionsTo()));
        model.addAttribute(MODEL_ATTRIBUTE_ANSWERS, viewConverter.toAnswersVo(questionnaireFacadeService.getAnswersTo()));
        return viewConverter.toPage(commonTo.getState(), false);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.QUESTION)
    @RequestMapping(value = "question", method = RequestMethod.POST)
    public String question(@ModelAttribute(MODEL_ATTRIBUTE_ANSWERS) AnswersVo answerVo, @RequestParam String action, BindingResult errors, Model model) throws Exception {
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
        return viewConverter.toPage(commonTo.getState(), true);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.FINISH)
    @RequestMapping(value = "finish")
    public String finish(Model model) throws Exception {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        model.addAttribute(MODEL_ATTRIBUTE_COMMON, viewConverter.toCommonVo(commonTo));
        return viewConverter.toPage(commonTo.getState(), false);
    }
}
