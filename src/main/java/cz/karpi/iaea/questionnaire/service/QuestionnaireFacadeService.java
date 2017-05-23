package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import cz.karpi.iaea.questionnaire.model.EQuestionType;
import cz.karpi.iaea.questionnaire.model.Element;
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
import cz.karpi.iaea.questionnaire.service.to.PlannerQuestionTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerTo;
import cz.karpi.iaea.questionnaire.service.to.QuestionTo;
import cz.karpi.iaea.questionnaire.service.to.QuestionsTo;

@Service
public class QuestionnaireFacadeService {

    private FormService formService;

    private FlowService flowService;

    private ValidateService validateService;

    @Autowired
    public QuestionnaireFacadeService(FormService formService, FlowService flowService, ValidateService validateService) {
        this.formService = formService;
        this.flowService = flowService;
        this.validateService = validateService;
    }

    public CommonTo getCommonTo() {
        final CommonTo commonTo = new CommonTo();
        commonTo.setCompanyName(formService.getCompanyName());
        commonTo.setState(flowService.getFlow().getFlowType().name());
        commonTo.setActions(flowService.getPossibilityActions());
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
        answersTo.setAnswerList(flowService.getCurrentSubCategory().getQuestions().stream()
                                    .map(this::mapToAnswer).collect(Collectors.toList()));
        return answersTo;
    }

    private AnswerTo mapToAnswer(Question question) {
        final AnswerTo answerTo = new AnswerTo();
        answerTo.setNumber(question.getNumber());
        answerTo.setComments(/*TODO*/"");
        if (question.getType().equals(EQuestionType.WITH_PIGRADE)) {
            answerTo.setPiGrade(/*TODO*/0);
        }
        return answerTo;
    }

    public Void question(AnswersTo answersTo, FlowService.EAction action) {
        if (action.equals(FlowService.EAction.NEXT)) {
            validateService.validate(answersTo);
            formService.saveAnswer(answersTo);
        }
        flowService.moveCounterTo(action);
        return null;
    }

    public void instruction(FlowService.EAction action) {
        flowService.moveCounterTo(action);
    }

    public AssessmentTo getAssessmentTo() {
        final AssessmentTo assessmentTo = new AssessmentTo();
        final SubCategory subCategory = flowService.getCurrentSubCategory();
        assessmentTo.setSubCategory(subCategory.getName());
        assessmentTo.setCategory(subCategory.getCategory().getName());
        assessmentTo.setAssessmentQuestions(getAnswersTo().getAnswerList().stream()
                                                .map(this::mapToAssessmentQuestion).collect(Collectors.toList()));
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
        final Question question = formService.getQuestion(answerTo.getNumber());
        assessmentQuestionTo.setNumber(question.getNumber());
        assessmentQuestionTo.setText(question.getQuestion());
        return assessmentQuestionTo;
    }

    public AssessmentAnswersTo getAssessmentAnswersTo() {
        final AssessmentAnswersTo assessmentAnswersTo = new AssessmentAnswersTo();
        assessmentAnswersTo.setAnswerList(flowService.getCurrentSubCategory().getQuestions().stream()
                                    .map(this::mapToAssessmentAnswer).collect(Collectors.toList()));
        return assessmentAnswersTo;
    }


    private AssessmentAnswerTo mapToAssessmentAnswer(Question question) {
        final AssessmentAnswerTo answerTo = new AssessmentAnswerTo();
        answerTo.setNumber(question.getNumber());
        answerTo.setPiGrades(formService.getElements().stream().collect(Collectors.toMap(e -> e, e -> /*TODO*/0)));
        return answerTo;
    }

    public Void assessment(AssessmentAnswersTo assessmentAnswersTo, FlowService.EAction action) {
        if (action.equals(FlowService.EAction.NEXT)) {
            formService.saveAssessmentAnswer(assessmentAnswersTo);
        }
        flowService.moveCounterTo(action);
        return null;
    }

    public PlannerTo getPlannerTo() {
        final PlannerTo plannerTo = new PlannerTo();
        final SubCategory subCategory = flowService.getCurrentSubCategory();
        plannerTo.setSubCategory(subCategory.getName());
        plannerTo.setCategory(subCategory.getCategory().getName());
        plannerTo.setPlannerQuestions(getAssessmentAnswersTo().getAnswerList().stream().map(this::mapToPlanner).collect(Collectors.toList()));
        plannerTo.setCurrentPage(flowService.getCurrentPage());
        plannerTo.setMaxPage(flowService.getMaxPage());
        plannerTo.setElements(formService.getElements().stream().map(this::mapToElement).collect(Collectors.toList()));
        plannerTo.setYears(formService.getYears());
        return plannerTo;
    }

    private PlannerQuestionTo mapToPlanner(AssessmentAnswerTo answerTo) {
        final PlannerQuestionTo plannerQuestionTo = new PlannerQuestionTo();
        final Question question = formService.getQuestion(answerTo.getNumber());
        plannerQuestionTo.setNumber(question.getNumber());
        plannerQuestionTo.setText(question.getQuestion());
        plannerQuestionTo.setPiGrade(answerTo.getPiGrades().entrySet().stream()
                                         .collect(Collectors.toMap(k -> k.getKey().getName(), Map.Entry::getValue)));
        return plannerQuestionTo;
    }

    public PlannerAnswersTo getPlannerAnswersTo() {
        final PlannerAnswersTo plannerAnswersTo = new PlannerAnswersTo();
        plannerAnswersTo.setAnswerList(flowService.getCurrentSubCategory().getQuestions().stream()
                                              .flatMap(this::mapToPlannerAnswer).collect(Collectors.toList()));
        return plannerAnswersTo;
    }

    private Stream<PlannerAnswerTo> mapToPlannerAnswer(Question question) {
        final List<PlannerAnswerTo> plannerAnswerToList = new ArrayList<>();
        formService.getElements().forEach(element -> {
            final PlannerAnswerTo plannerAnswerTo = new PlannerAnswerTo();
            plannerAnswerTo.setElement(element);
            plannerAnswerTo.setNumber(question.getNumber());
            plannerAnswerTo.setPlanned(formService.getYears().stream().flatMap(year -> year.getQuarters().stream())
                                           .collect(Collectors.toMap(quarter -> quarter, quarter -> /*TODO*/Boolean.FALSE)));
            plannerAnswerTo.setOwnership(/*TODO*/"");
            plannerAnswerTo.setTask(/*TODO*/"");
            plannerAnswerToList.add(plannerAnswerTo);
        });
        return plannerAnswerToList.stream();
    }

    public Void planner(PlannerAnswersTo plannerAnswersTo, FlowService.EAction action) {
        if (action.equals(FlowService.EAction.NEXT)) {
            formService.savePlannerAnswer(plannerAnswersTo);
        }
        flowService.moveCounterTo(action);
        return null;
    }
}
