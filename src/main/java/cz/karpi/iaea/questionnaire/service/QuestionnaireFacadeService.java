package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cz.karpi.iaea.questionnaire.model.AnswerRow;
import cz.karpi.iaea.questionnaire.model.AssessmentRow;
import cz.karpi.iaea.questionnaire.model.Element;
import cz.karpi.iaea.questionnaire.model.Flow;
import cz.karpi.iaea.questionnaire.model.PlannerRow;
import cz.karpi.iaea.questionnaire.model.Question;
import cz.karpi.iaea.questionnaire.model.SubCategory;
import cz.karpi.iaea.questionnaire.service.to.AnswerTo;
import cz.karpi.iaea.questionnaire.service.to.AnswersTo;
import cz.karpi.iaea.questionnaire.service.to.AssessmentAnswerTo;
import cz.karpi.iaea.questionnaire.service.to.AssessmentAnswersTo;
import cz.karpi.iaea.questionnaire.service.to.AssessmentQuestionTo;
import cz.karpi.iaea.questionnaire.service.to.AssessmentTo;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.service.to.ElementTo;
import cz.karpi.iaea.questionnaire.service.to.InitTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerAnswerTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerAnswersTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerOverviewTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerQuestionTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerTo;
import cz.karpi.iaea.questionnaire.service.to.QuestionTo;
import cz.karpi.iaea.questionnaire.service.to.QuestionsTo;

@Service
public class QuestionnaireFacadeService {

    private FormService formService;

    private FlowService flowService;

    private ValidateService validateService;

    private PrintService printService;

    private final SavingStatusService savingStatusService;

    private Boolean useAllAnswerTo = Boolean.FALSE;

    @Autowired
    public QuestionnaireFacadeService(FormService formService, FlowService flowService, ValidateService validateService,
                                      SavingStatusService savingStatusService, PrintService printService) {
        this.formService = formService;
        this.flowService = flowService;
        this.validateService = validateService;
        this.savingStatusService = savingStatusService;
        this.printService = printService;
    }

    public void reset() {
        formService.reset();
        flowService.reset();
    }

    public CommonTo getCommonTo() {
        final CommonTo commonTo = new CommonTo();
        commonTo.setCompanyName(formService.getCompanyName());
        commonTo.setState(flowService.getFlow().getFlowType().name());
        commonTo.setActions(flowService.getPossibilityActions());
        commonTo.setCategories(formService.getCategories());
        commonTo.setQuestionnaireMenu(formService.getQuestionnaireMenu());
        commonTo.setAssessmentMenu(formService.getAssessmentMenu());
        commonTo.setPlannerMenu(formService.getPlannerMenu());
        return commonTo;
    }

    public Void init(InitTo initTo) {
        validateService.validate(initTo);
        formService.saveInit(initTo);
        formService.loadDefinitions();
        flowService.moveCounterToNext();
        return null;
    }

    public QuestionsTo getQuestionsTo() {
        final QuestionsTo questionsTo = new QuestionsTo();
        final SubCategory subCategory = flowService.getCurrentSubCategory();
        questionsTo.setSubCategory(subCategory.getName());
        questionsTo.setCategory(subCategory.getCategory().getName());
        questionsTo.setQuestions(subCategory.getQuestions().stream().map(this::mapToQuestion).collect(Collectors.toList()));
        questionsTo.setCurrentPage(flowService.getCurrentPage());
        questionsTo.setMaxPage(flowService.getMaxPage());
        return questionsTo;
    }

    private QuestionTo mapToQuestion(Question question) {
        final QuestionTo questionTo = new QuestionTo();
        questionTo.setNumber(question.getNumber());
        questionTo.setText(question.getQuestion());
        questionTo.setType(question.getType().name());
        return questionTo;
    }

    public AnswersTo getAnswersTo() {
        final AnswersTo answersTo = new AnswersTo();
        answersTo.setAnswerList(getAnswerList());
        return answersTo;
    }

    private List<AnswerTo> getAnswerList() {
        final Stream<Question> questionStream;
        if (!useAllAnswerTo) {
            questionStream = flowService.getCurrentSubCategory().getQuestions().stream();
        } else {
            questionStream = formService.getCategories().stream().flatMap(category -> category.getSubCategories().stream())
                .flatMap(subCategory -> subCategory.getQuestions().stream());
        }
        return questionStream.map(this::mapToAnswer).collect(Collectors.toList());
    }

    private AnswerTo mapToAnswer(Question question) {
        final AnswerTo answerTo = new AnswerTo();
        answerTo.setNumber(question.getNumber());
        final AnswerRow answerRow = formService.getAnswerRow(question);
        answerTo.setComments(answerRow.getComments());
        answerTo.setPiGrade(answerRow.getPiGrade());
        return answerTo;
    }

    public Void question(AnswersTo answersTo, FlowService.EAction action, Flow.EFlowType nextStep, String category, String subCategory) {
        processAction(
                () -> {
                    validateService.validate(answersTo);
                    formService.saveAnswer(answersTo);
                    return null;
                    },
                action,
                nextStep,
                category,
                subCategory);
        return null;
    }

    public void instruction(FlowService.EAction action) {
        this.instruction(action, null,  null, null);
    }

    public void instruction(FlowService.EAction action, Flow.EFlowType nextStep, String category, String subCategory) {
        flowService.moveCounterTo(action, nextStep, category, subCategory);
    }

    public AssessmentTo getAssessmentTo() {
        final AssessmentTo assessmentTo = new AssessmentTo();
        final SubCategory subCategory = flowService.getCurrentSubCategory();
        assessmentTo.setSubCategory(subCategory.getName());
        assessmentTo.setCategory(subCategory.getCategory().getName());
        assessmentTo.setAssessmentQuestions(filter(getAnswersTo()).map(this::mapToAssessmentQuestion).collect(Collectors.toList()));
        assessmentTo.setCurrentPage(flowService.getCurrentPage());
        assessmentTo.setMaxPage(flowService.getMaxPage());
        assessmentTo.setElements(formService.getElements().stream().map(this::mapToElement).collect(Collectors.toList()));
        return assessmentTo;
    }

    private ElementTo mapToElement(Element element) {
        final ElementTo elementTo = new ElementTo();
        elementTo.setName(element.getName());
        return elementTo;
    }

    private AssessmentQuestionTo mapToAssessmentQuestion(AnswerTo answerTo) {
        final AssessmentQuestionTo assessmentQuestionTo = new AssessmentQuestionTo();
        assessmentQuestionTo.setAnswer(answerTo.getComments());
        assessmentQuestionTo.setPiGrade(answerTo.getPiGrade());
        final Question question = formService.getQuestion(answerTo.getNumber());
        assessmentQuestionTo.setNumber(question.getNumber());
        assessmentQuestionTo.setText(question.getQuestion());
        return assessmentQuestionTo;
    }

    public AssessmentAnswersTo getAssessmentAnswersTo() {
        final AssessmentAnswersTo assessmentAnswersTo = new AssessmentAnswersTo();
        assessmentAnswersTo.setAnswerList(filter(getAnswersTo()).map(this::mapToAssessmentAnswer).collect(Collectors.toList()));
        return assessmentAnswersTo;
    }


    private AssessmentAnswerTo mapToAssessmentAnswer(AnswerTo answerTo) {
        final AssessmentAnswerTo assessmentAnswerTo = new AssessmentAnswerTo();
        assessmentAnswerTo.setNumber(answerTo.getNumber());
        final AssessmentRow assessmentRow = formService.getAssessmentRow(formService.getQuestion(answerTo.getNumber()));
        assessmentAnswerTo.setPiGrades(formService.getElements().stream().collect(HashMap::new, (m, e) -> m.put(e, assessmentRow.getElementsPiGrade().get(e)), HashMap::putAll));
        return assessmentAnswerTo;
    }

    private Stream<AnswerTo> filter(AnswersTo answersTo) {
        return answersTo.getAnswerList().stream()
            .filter(answerTo -> answerTo.getPiGrade() != null);
    }

    public Void assessment(AssessmentAnswersTo assessmentAnswersTo, FlowService.EAction action, Flow.EFlowType nextStep, String category, String subCategory) {
        processAction(
                () -> { formService.saveAssessmentAnswer(assessmentAnswersTo); return null; },
                action,
                nextStep,
                category,
                subCategory
        );
        return null;
    }

    public PlannerTo getPlannerTo() {
        final PlannerTo plannerTo = new PlannerTo();
        final SubCategory subCategory = flowService.getCurrentSubCategory();
        plannerTo.setSubCategory(subCategory.getName());
        plannerTo.setCategory(subCategory.getCategory().getName());
        plannerTo.setPlannerQuestions(filter(getAssessmentAnswersTo()).map(this::mapToPlanner).collect(Collectors.toList()));
        plannerTo.setCurrentPage(flowService.getCurrentPage());
        plannerTo.setMaxPage(flowService.getMaxPage());
        plannerTo.setYears(formService.getYears());
        return plannerTo;
    }

    private Stream<AssessmentAnswerTo> filter(AssessmentAnswersTo assessmentAnswersTo) {
        return assessmentAnswersTo.getAnswerList().stream()
            .filter(assessmentAnswerTo -> assessmentAnswerTo.getPiGrades().values().stream().anyMatch(Objects::nonNull));
    }

    private PlannerQuestionTo mapToPlanner(AssessmentAnswerTo answerTo) {
        final PlannerQuestionTo plannerQuestionTo = new PlannerQuestionTo();
        final Question question = formService.getQuestion(answerTo.getNumber());
        plannerQuestionTo.setNumber(question.getNumber());
        plannerQuestionTo.setText(question.getQuestion());
        plannerQuestionTo.setPiGrades(filter2(answerTo).collect(HashMap::new, (m, e) -> m.put(e.getKey().getName(), e.getValue()), HashMap::putAll));
        return plannerQuestionTo;
    }

    public PlannerAnswersTo getPlannerAnswersTo() {
        final PlannerAnswersTo plannerAnswersTo = new PlannerAnswersTo();
        plannerAnswersTo.setAnswerList(filter(getAssessmentAnswersTo()).flatMap(this::mapToPlannerAnswer).collect(Collectors.toList()));
        return plannerAnswersTo;
    }

    private Stream<Map.Entry<Element, Integer>> filter2(AssessmentAnswerTo assessmentAnswerTo) {
        return assessmentAnswerTo.getPiGrades().entrySet().stream().filter(element -> element.getValue() != null);
    }

    private Stream<PlannerAnswerTo> mapToPlannerAnswer(AssessmentAnswerTo assessmentAnswerTo) {
        return filter2(assessmentAnswerTo).map(element -> {
                final PlannerAnswerTo plannerAnswerTo = new PlannerAnswerTo();
                final PlannerRow plannerRow = formService.getPlannerRow(formService.getQuestion(assessmentAnswerTo.getNumber()), element.getKey());
                plannerAnswerTo.setElement(element.getKey());
                plannerAnswerTo.setNumber(assessmentAnswerTo.getNumber());
                plannerAnswerTo.setPlanned(formService.getYears().stream().flatMap(year -> year.getQuarters().stream())
                                           .collect(HashMap::new, (m, quarter) -> m.put(quarter, plannerRow.getPlanned().get(quarter)), HashMap::putAll));
                plannerAnswerTo.setOwnership(plannerRow.getOwnership());
                plannerAnswerTo.setTask(plannerRow.getTask());
                return plannerAnswerTo;
            });
    }

    public Void planner(PlannerAnswersTo plannerAnswersTo, FlowService.EAction action, Flow.EFlowType nextStep, String category, String subCategory) {
        processAction(
                () -> { formService.savePlannerAnswer(plannerAnswersTo); return null; },
                action,
                nextStep,
                category, 
		subCategory);

        if (flowService.getPossibilityActions().contains(FlowService.EAction.FINISH)) {
            formService.saveCdp();
        }
        return null;
    }

    public Void cdp(FlowService.EAction action) {
        processAction(() -> null, action);
        if (action.equals(FlowService.EAction.PRINT)) {
            printService.print(getCommonTo(), getPlannerOverviewTo());
        }
        return null;
    }

    public List<String> getExistedCompanies() {
        return formService.getExistedCompanies();
    }

    private void processAction(Supplier<Void> supplier, FlowService.EAction action) {
        this.processAction(supplier, action, null, null, null);
    }

    private void processAction(Supplier<Void> supplier, FlowService.EAction action, Flow.EFlowType nextStep, String category, String subCategory) {
        // TODO: 2. 7. 2017 reorganize if else if else....
        if (action.equals(FlowService.EAction.GOTO)) {
            supplier.get();
            flowService.moveCounterToPosition(nextStep, category, subCategory);
        }
        if (action.equals(FlowService.EAction.NEXT) || action.equals(FlowService.EAction.SAVE)) {
            supplier.get();
        }
        if (!action.equals(FlowService.EAction.SAVE) && !action.equals(FlowService.EAction.PRINT)) {
            flowService.moveCounterTo(action);
        } else {
            savingStatusService.waitForSave();
        }
    }

    public Integer savingProgress() {
        return savingStatusService.savingProgress();
    }

    public PlannerOverviewTo getPlannerOverviewTo() {
        final PlannerOverviewTo plannerOverviewTo = new PlannerOverviewTo();
        useAllAnswerTo = Boolean.TRUE;
        plannerOverviewTo.setPlannerQuestions(filter(getAssessmentAnswersTo()).map(this::mapToPlanner).collect(Collectors.toList()));
        plannerOverviewTo.setYears(formService.getYears());
        plannerOverviewTo.setValue(getPlannerAnswersTo());
        useAllAnswerTo = Boolean.FALSE;
        return plannerOverviewTo;
    }
}
