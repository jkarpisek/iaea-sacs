package cz.karpi.iaea.questionnaire.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cz.karpi.iaea.questionnaire.model.Flow;
import cz.karpi.iaea.questionnaire.service.QuestionnaireFacadeService;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.web.converter.ViewConverter;
import cz.karpi.iaea.questionnaire.web.interceptor.FlowInterceptor;
import cz.karpi.iaea.questionnaire.web.model.AnswersVo;
import cz.karpi.iaea.questionnaire.web.model.InitVo;

/**
 * Created by karpi on 12.4.17.
 */
@Controller
public class QuestionnaireController {

    private static final String MODEL_ATTRIBUTE_INIT = "init";
    private static final String MODEL_ATTRIBUTE_ANSWERS = "answers";
    private static final String MODEL_ATTRIBUTE_QUESTION = "questions";
    private static final String MODEL_ATTRIBUTE_ASSESSMENT = "assessment";
    private static final String MODEL_ATTRIBUTE_PLANNER = "planner";
    private static final String MODEL_ATTRIBUTE_CDP = "cdp";

    private final QuestionnaireFacadeService questionnaireFacadeService;

    private final ViewConverter viewConverter;

    private final ControllerUtils controllerUtils;

    @Autowired
    public QuestionnaireController(QuestionnaireFacadeService questionnaireFacadeService, ViewConverter viewConverter, ControllerUtils controllerUtils) {
        this.questionnaireFacadeService = questionnaireFacadeService;
        this.viewConverter = viewConverter;
        this.controllerUtils = controllerUtils;
    }

    @FlowInterceptor.FlowCheck({Flow.EFlowType.START, Flow.EFlowType.INSTRUCTION, Flow.EFlowType.QUESTION, Flow.EFlowType.ASSESSMENT, Flow.EFlowType.PLANNER, Flow.EFlowType.CDP})
    @RequestMapping("/*")
    public String index() {
        final CommonTo commonTo = questionnaireFacadeService.getCommonTo();
        return controllerUtils.toPage(commonTo.getState(), true);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.START)
    @RequestMapping("/start")
    public String start(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_INIT, new InitVo());
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.START)
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String start(@ModelAttribute(MODEL_ATTRIBUTE_INIT) InitVo initVo, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.init(viewConverter.toInitTo(initVo)));
        return controllerUtils.returnPost(model, errors);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.INSTRUCTION)
    @RequestMapping(value = "/instruction")
    public String instruction(Model model) {
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.INSTRUCTION)
    @RequestMapping(value = "instruction", method = RequestMethod.POST)
    public String instruction(Model model, @RequestParam String action) {
        questionnaireFacadeService.instruction(viewConverter.convertToEAction(action));
        return controllerUtils.returnPost(model, null);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.QUESTION)
    @RequestMapping(value = "/question")
    public String question  (Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_QUESTION, viewConverter.toQuestionsVo(questionnaireFacadeService.getQuestionsTo()));
        model.addAttribute(MODEL_ATTRIBUTE_ANSWERS, viewConverter.toAnswersVo(questionnaireFacadeService.getAnswersTo()));
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.QUESTION)
    @RequestMapping(value = "/question", method = RequestMethod.POST)
    public String question(@ModelAttribute(MODEL_ATTRIBUTE_ANSWERS) AnswersVo answerVo, @RequestParam String action, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.question(viewConverter.toAnswersTo(answerVo), viewConverter.convertToEAction(action)));
        return controllerUtils.returnPost(model, errors);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.ASSESSMENT)
    @RequestMapping(value = "/assessment")
    public String assessment(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_ASSESSMENT, viewConverter.toAssessmentVo(null));
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.ASSESSMENT)
    @RequestMapping(value = "/assessment", method = RequestMethod.POST)
    public String assessmentPost(@ModelAttribute(MODEL_ATTRIBUTE_ASSESSMENT) AnswersVo answerVo, @RequestParam String action, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.assessment(viewConverter.convertToEAction(action)));
        return controllerUtils.returnPost(model, errors);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.PLANNER)
    @RequestMapping(value = "/planner")
    public String planner(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_PLANNER, viewConverter.toPlannerVo(null));
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.PLANNER)
    @RequestMapping(value = "/planner", method = RequestMethod.POST)
    public String plannerPost(@ModelAttribute(MODEL_ATTRIBUTE_PLANNER) AnswersVo answerVo, @RequestParam String action, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.planner(viewConverter.convertToEAction(action)));
        return controllerUtils.returnPost(model, errors);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.CDP)
    @RequestMapping(value = "/cdp")
    public String cdp(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_CDP, viewConverter.toCdpVo(null));
        return controllerUtils.returnGet(model);
    }
}
