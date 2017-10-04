package cz.karpi.iaea.questionnaire.web.converter;

import cz.karpi.iaea.questionnaire.model.Flow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.karpi.iaea.questionnaire.model.Quarter;
import cz.karpi.iaea.questionnaire.service.FlowService;
import cz.karpi.iaea.questionnaire.service.to.AnswersTo;
import cz.karpi.iaea.questionnaire.service.to.AssessmentAnswersTo;
import cz.karpi.iaea.questionnaire.service.to.AssessmentQuestionTo;
import cz.karpi.iaea.questionnaire.service.to.AssessmentTo;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.service.to.InitTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerAnswerTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerAnswersTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerOverviewTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerQuestionTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerTo;
import cz.karpi.iaea.questionnaire.service.to.QuestionTo;
import cz.karpi.iaea.questionnaire.service.to.QuestionsTo;
import cz.karpi.iaea.questionnaire.web.interceptor.QuestionnaireDialectUtils;
import cz.karpi.iaea.questionnaire.web.model.CommonVo;
import cz.karpi.iaea.questionnaire.web.model.InitVo;
import cz.karpi.iaea.questionnaire.web.model.MatrixMap;
import cz.karpi.iaea.questionnaire.web.model.MatrixModel;

/**
 * Created by karpi on 15.4.17.
 */
@Component
public class ViewConverter {

    private static final String FIELD_ANSWER_PIGRADE = "piGrade";
    private static final String FIELD_ANSWER_COMMENTS = "comments";
    private static final String FIELD_PLANNER_ANSWER_TASK = "task";
    private static final String FIELD_PLANNER_ANSWER_OWNERSHIP = "ownership";

    private final QuestionnaireDialectUtils questionnaireDialectUtils;

    @Autowired
    public ViewConverter(QuestionnaireDialectUtils questionnaireDialectUtils) {
        this.questionnaireDialectUtils = questionnaireDialectUtils;
    }

    public MatrixModel toQuestionsFormVo(AnswersTo answersTo) {
        final MatrixModel answersVo = new MatrixModel();
        final MatrixMap matrixMap = answersVo.getValue();
        answersTo.getAnswerList().forEach(answerTo -> {
            matrixMap.put(questionnaireDialectUtils.getPropertyName(answerTo.getNumber(), FIELD_ANSWER_COMMENTS), answerTo.getComments());
            matrixMap.put(questionnaireDialectUtils.getPropertyName(answerTo.getNumber(), FIELD_ANSWER_PIGRADE), answerTo.getPiGrade());
        });
        return answersVo;
    }

    public AnswersTo toAnswersTo(MatrixModel answersVo, AnswersTo answersTo) {
        final MatrixMap matrixMap = answersVo.getValue();
        answersTo.getAnswerList().forEach(answerTo -> {
            final Object comments = matrixMap.get(questionnaireDialectUtils.getPropertyName(answerTo.getNumber(), FIELD_ANSWER_COMMENTS));
            answerTo.setComments(comments != null ? comments.toString() : null);
            final Object piGrade = matrixMap.get(questionnaireDialectUtils.getPropertyName(answerTo.getNumber(), FIELD_ANSWER_PIGRADE));
            answerTo.setPiGrade(piGrade != null && piGrade.toString().matches("[0-9]+")? Integer.valueOf(piGrade.toString()) : null);
        });
        return answersTo;
    }

    public InitTo toInitTo(InitVo initVo) {
        final InitTo initTo = new InitTo();
        initTo.setCompanyName(initVo.getCompanyName());
        return initTo;
    }

    public CommonVo toCommonVo(CommonTo commonTo) {
        final CommonVo commonVo = new CommonVo();
        commonVo.setCompanyName(commonTo.getCompanyName());
        commonVo.setActions(commonTo.getActions().stream().map(FlowService.EAction::name).collect(Collectors.toList()));
        commonVo.setCategories(commonTo.getCategories());
        commonVo.setQuestionnaireMenu(commonTo.getQuestionnaireMenu());
        commonVo.setAssessmentMenu(commonTo.getAssessmentMenu());
        commonVo.setPlannerMenu(commonTo.getPlannerMenu());
        return commonVo;
    }

    public Map<String, Object> toQuestionsMetaVo(QuestionsTo questionsTo) {
        final Map<String, Object> map = new HashMap<>();
        map.put("category", questionsTo.getCategory());
        map.put("subCategory", questionsTo.getSubCategory());
        map.put("currentPage", questionsTo.getCurrentPage());
        map.put("maxPage", questionsTo.getMaxPage());
        map.put("sacsAnswers", questionsTo.getQuestions().stream().map(this::mapQuestionVo).collect(Collectors.toList()));
        return map;
    }

    private Map<String, Object> mapQuestionVo(QuestionTo questionTo) {
        final Map<String, Object> question = new HashMap<>();
        question.put("number", questionTo.getNumber());
        question.put("question", questionTo.getText());
        question.put("type", questionTo.getType());
        return question;
    }

    public FlowService.EAction convertToEAction(String action) {
        return FlowService.EAction.valueOf(action.toUpperCase());
    }

    public Flow.EFlowType convertToEFlowType(String nextStep) {
        if (nextStep == null) return null;
        return Flow.EFlowType.valueOf(nextStep.toUpperCase());
    }

    public Map<String, Object> toAssessmentMetaVo(AssessmentTo assessmentTo) {
        final Map<String, Object> map = new HashMap<>();
        map.put("category", assessmentTo.getCategory());
        map.put("subCategory", assessmentTo.getSubCategory());
        map.put("elements", assessmentTo.getElements());
        map.put("sacsAnswers", assessmentTo.getAssessmentQuestions().stream().map(this::mapAssessmentVo).collect(Collectors.toList()));
        map.put("currentPage", assessmentTo.getCurrentPage());
        map.put("maxPage", assessmentTo.getMaxPage());
        return map;
    }

    private Map<String, Object> mapAssessmentVo(AssessmentQuestionTo questionTo) {
        final Map<String, Object> question = new HashMap<>();
        question.put("number", questionTo.getNumber());
        question.put("question", questionTo.getText());
        question.put("type", questionTo.getType());
        question.put("answer", questionTo.getAnswer());
        question.put("piGrade", questionTo.getPiGrade());
        return question;
    }

    private Map<String, Object> mapPlannerVo(PlannerQuestionTo questionTo) {
        final Map<String, Object> question = new HashMap<>();
        question.put("number", questionTo.getNumber());
        question.put("question", questionTo.getText());
        question.put("type", questionTo.getType());
        question.put("answer", questionTo.getAnswer());
        question.put("piGrade", questionTo.getPiGrade());
        question.put("piGrades", questionTo.getPiGrades());
        return question;
    }

    public MatrixModel toAssessmentFormVo(AssessmentAnswersTo assessmentAnswersTo) {
        final MatrixModel assessmentVo = new MatrixModel();
        assessmentAnswersTo.getAnswerList().forEach(assessmentAnswerTo ->
            assessmentAnswerTo.getPiGrades().forEach(
                (key, value) -> assessmentVo.getValue().put(questionnaireDialectUtils.getPropertyName(assessmentAnswerTo.getNumber(), key.getName()), value)
            )
        );
        return assessmentVo;
    }

    public AssessmentAnswersTo toAssessmentAnswerTo(MatrixModel assessmentVo, AssessmentAnswersTo assessmentAnswersTo) {
        final MatrixMap matrixMap = assessmentVo.getValue();
        assessmentAnswersTo.getAnswerList().forEach(assessmentAnswerTo ->
            assessmentAnswerTo.getPiGrades().forEach((key, value) -> {
                final Object piGrade = matrixMap.get(questionnaireDialectUtils.getPropertyName(assessmentAnswerTo.getNumber(), key.getName()));
                assessmentAnswerTo.getPiGrades().put(key, piGrade != null && piGrade.toString().matches("[0-9]+")? Integer.valueOf(piGrade.toString()) : null);
            })
        );
        return assessmentAnswersTo;
    }

    public Map<String, Object> toPlannerMetaVo(PlannerTo plannerTo) {
        final Map<String, Object> map = new HashMap<>();
        map.put("category", plannerTo.getCategory());
        map.put("subCategory", plannerTo.getSubCategory());
        map.put("years", plannerTo.getYears());
        map.put("sacsAnswers", plannerTo.getPlannerQuestions().stream().map(this::mapPlannerVo).collect(Collectors.toList()));
        map.put("currentPage", plannerTo.getCurrentPage());
        map.put("maxPage", plannerTo.getMaxPage());
        return map;
    }

    public MatrixModel toPlannerFormVo(PlannerAnswersTo plannerAnswersTo) {
        final MatrixModel plannerVo = new MatrixModel();
        plannerAnswersTo.getAnswerList().forEach(plannerAnswerTo -> {
            final String prefix = getPlannerKey(plannerAnswerTo);
            plannerVo.getValue().put(questionnaireDialectUtils.getPropertyName(prefix, FIELD_PLANNER_ANSWER_TASK), plannerAnswerTo.getTask());
            plannerVo.getValue().put(questionnaireDialectUtils.getPropertyName(prefix, FIELD_PLANNER_ANSWER_OWNERSHIP), plannerAnswerTo.getOwnership());
            plannerAnswerTo.getPlanned().forEach(
                (quarter, value) -> plannerVo.getValue().put(questionnaireDialectUtils.getPropertyName(prefix, getQuarterKey(quarter)), value)
            );
        });
        return plannerVo;
    }

    private String getPlannerKey(PlannerAnswerTo plannerAnswerTo) {
        return plannerAnswerTo.getNumber() + "-" + plannerAnswerTo.getElement().getName();
    }

    private String getQuarterKey(Quarter quarter) {
        return quarter.getYear().getName() + "-" + quarter.getName();
    }

    public PlannerAnswersTo toPlannerAnswerTo(MatrixModel plannerVo, PlannerAnswersTo plannerAnswersTo) {
        final MatrixMap matrixMap = plannerVo.getValue();
        plannerAnswersTo.getAnswerList().forEach(plannerAnswerTo -> {
            final String prefix = getPlannerKey(plannerAnswerTo);
            plannerAnswerTo.setTask(matrixMap.get(questionnaireDialectUtils.getPropertyName(prefix, FIELD_PLANNER_ANSWER_TASK)).toString());
            plannerAnswerTo.setOwnership(matrixMap.get(questionnaireDialectUtils.getPropertyName(prefix, FIELD_PLANNER_ANSWER_OWNERSHIP)).toString());
            plannerAnswerTo.getPlanned().forEach(
                (quarter, value) -> plannerAnswerTo.getPlanned().put(quarter, Boolean.valueOf(String.valueOf(matrixMap.get(questionnaireDialectUtils.getPropertyName(prefix, getQuarterKey(quarter))))))
            );
        });
        return plannerAnswersTo;
    }

    public Map<String, Object> toCdpMetaVo(PlannerOverviewTo plannerOverviewTo) {
        final Map<String, Object> map = new HashMap<>();
        map.put("years", plannerOverviewTo.getYears());
        map.put("sacsAnswers", plannerOverviewTo.getPlannerQuestions().stream().map(this::mapPlannerVo).collect(Collectors.toList()));
        map.put("value", toCdpValueVo(plannerOverviewTo.getValue()).getValue());
        return map;
    }

    private MatrixModel toCdpValueVo(PlannerAnswersTo plannerAnswersTo) {
        final MatrixModel plannerVo = new MatrixModel();
        plannerAnswersTo.getAnswerList().forEach(plannerAnswerTo -> {
            final String prefix = getPlannerKey(plannerAnswerTo);
            plannerVo.getValue().put(questionnaireDialectUtils.getPropertyName(prefix, FIELD_PLANNER_ANSWER_TASK), plannerAnswerTo.getTask());
            plannerVo.getValue().put(questionnaireDialectUtils.getPropertyName(prefix, FIELD_PLANNER_ANSWER_OWNERSHIP), plannerAnswerTo.getOwnership());
            plannerAnswerTo.getPlanned().forEach(
                (quarter, value) -> plannerVo.getValue().put(questionnaireDialectUtils.getPropertyName(prefix, getQuarterKey(quarter)), value)
            );
        });
        return plannerVo;
    }

    public InitVo toInitFormVo() {
        return new InitVo();
    }

    public Map<String, Object> toInitMetaVo(List<String> existedCompanies) {
        final Map<String, Object> map = new HashMap<>();
        map.put("existedCompanies", existedCompanies.stream().map(str -> "'" + str + "'").collect(Collectors.joining(",")));
        return map;
    }
}
