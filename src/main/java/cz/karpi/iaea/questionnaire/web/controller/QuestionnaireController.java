package cz.karpi.iaea.questionnaire.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import cz.karpi.iaea.questionnaire.model.Flow;
import cz.karpi.iaea.questionnaire.service.QuestionnaireFacadeService;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.web.converter.ViewConverter;
import cz.karpi.iaea.questionnaire.web.interceptor.FlowInterceptor;
import cz.karpi.iaea.questionnaire.web.model.InitVo;
import cz.karpi.iaea.questionnaire.web.model.MatrixModel;

import static cz.karpi.iaea.questionnaire.web.controller.ControllerUtils.MODEL_ATTRIBUTE_FORM;
import static cz.karpi.iaea.questionnaire.web.controller.ControllerUtils.MODEL_ATTRIBUTE_META;

/**
 * Created by karpi on 12.4.17.
 */
@Controller
public class QuestionnaireController {

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
        questionnaireFacadeService.reset();
        model.addAttribute(MODEL_ATTRIBUTE_META, viewConverter.toInitMetaVo(questionnaireFacadeService.getExistedCompanies()));
        model.addAttribute(MODEL_ATTRIBUTE_FORM, viewConverter.toInitFormVo());
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.START)
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String start(@ModelAttribute(MODEL_ATTRIBUTE_FORM) InitVo initVo, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.init(viewConverter.toInitTo(initVo)));
        return controllerUtils.returnPost(model, () -> viewConverter.toInitMetaVo(questionnaireFacadeService.getExistedCompanies()), errors);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.INSTRUCTION)
    @RequestMapping("/instruction")
    public String instruction(Model model) {
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.INSTRUCTION)
    @RequestMapping(value = "/instruction", method = RequestMethod.POST)
    public String instruction(Model model, @RequestParam String action) {
        questionnaireFacadeService.instruction(viewConverter.convertToEAction(action));
        return controllerUtils.returnPost(model, null,null);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.QUESTION)
    @RequestMapping("/question")
    public String question  (Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_META, viewConverter.toQuestionsMetaVo(questionnaireFacadeService.getQuestionsTo()));
        model.addAttribute(MODEL_ATTRIBUTE_FORM, viewConverter.toQuestionsFormVo(questionnaireFacadeService.getAnswersTo()));
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.QUESTION)
    @RequestMapping(value = "/question", method = RequestMethod.POST)
    public String question(@ModelAttribute(MODEL_ATTRIBUTE_FORM) MatrixModel answerVo, @RequestParam String action, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.question(viewConverter.toAnswersTo(answerVo, questionnaireFacadeService.getAnswersTo()), viewConverter.convertToEAction(action)));
        return controllerUtils.returnPost(model, () -> viewConverter.toQuestionsMetaVo(questionnaireFacadeService.getQuestionsTo()), errors);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.ASSESSMENT)
    @RequestMapping("/assessment")
    public String assessment(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_META, viewConverter.toAssessmentMetaVo(questionnaireFacadeService.getAssessmentTo()));
        model.addAttribute(MODEL_ATTRIBUTE_FORM, viewConverter.toAssessmentFormVo(questionnaireFacadeService.getAssessmentAnswersTo()));
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.ASSESSMENT)
    @RequestMapping(value = "/assessment", method = RequestMethod.POST)
    public String assessmentPost(@ModelAttribute(MODEL_ATTRIBUTE_FORM) MatrixModel assessmentVo, @RequestParam String action, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.assessment(viewConverter.toAssessmentAnswerTo(assessmentVo, questionnaireFacadeService.getAssessmentAnswersTo()), viewConverter.convertToEAction(action)));
        return controllerUtils.returnPost(model, () -> viewConverter.toAssessmentMetaVo(questionnaireFacadeService.getAssessmentTo()), errors);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.PLANNER)
    @RequestMapping("/planner")
    public String planner(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_META, viewConverter.toPlannerMetaVo(questionnaireFacadeService.getPlannerTo()));
        model.addAttribute(MODEL_ATTRIBUTE_FORM, viewConverter.toPlannerFormVo(questionnaireFacadeService.getPlannerAnswersTo()));
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.PLANNER)
    @RequestMapping(value = "/planner", method = RequestMethod.POST)
    public String plannerPost(@ModelAttribute(MODEL_ATTRIBUTE_FORM) MatrixModel plannerVo, @RequestParam String action, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.planner(viewConverter.toPlannerAnswerTo(plannerVo, questionnaireFacadeService.getPlannerAnswersTo()), viewConverter.convertToEAction(action)));
        return controllerUtils.returnPost(model, () -> viewConverter.toPlannerMetaVo(questionnaireFacadeService.getPlannerTo()), errors);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.CDP)
    @RequestMapping("/cdp")
    public String cdp(Model model) {
        model.addAttribute(MODEL_ATTRIBUTE_META, viewConverter.toCdpMetaVo(questionnaireFacadeService.getPlannerOverviewTo()));
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck(Flow.EFlowType.CDP)
    @RequestMapping(value = "/cdp", method = RequestMethod.POST)
    public String cdpPost(@ModelAttribute(MODEL_ATTRIBUTE_FORM) MatrixModel cdpVo, @RequestParam String action, BindingResult errors, Model model) {
        controllerUtils.catchValidationException(errors, () -> questionnaireFacadeService.cdp(viewConverter.convertToEAction(action)));
        return controllerUtils.returnPost(model, () -> null, errors);
    }

    @FlowInterceptor.FlowCheck({Flow.EFlowType.START, Flow.EFlowType.INSTRUCTION, Flow.EFlowType.QUESTION, Flow.EFlowType.ASSESSMENT, Flow.EFlowType.PLANNER, Flow.EFlowType.CDP})
    @RequestMapping("/reset")
    public String reset(Model model) {
        questionnaireFacadeService.reset();
        return controllerUtils.returnGet(model);
    }

    @FlowInterceptor.FlowCheck({Flow.EFlowType.START, Flow.EFlowType.INSTRUCTION, Flow.EFlowType.QUESTION, Flow.EFlowType.ASSESSMENT, Flow.EFlowType.PLANNER, Flow.EFlowType.CDP})
    @RequestMapping("/savingProgress")
    @ResponseBody
    public Map<String, Integer> savingProgress() {
        return new HashMap<String, Integer>() {{ put("progress", questionnaireFacadeService.savingProgress()); }};
    }
}
