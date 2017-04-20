package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import cz.karpi.iaea.questionnaire.model.AbstractAnswerRow;
import cz.karpi.iaea.questionnaire.model.SubCategory;
import cz.karpi.iaea.questionnaire.service.to.AnswerTo;
import cz.karpi.iaea.questionnaire.service.to.AnswersTo;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.service.to.InitTo;
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

    public void init(InitTo initTo) {
        validateService.validate(initTo);
        formService.saveInit(initTo);
        formService.loadSACSForm();
        flowService.moveCounterToNext();
    }

    public AnswersTo getAnswersTo() {
        final AnswersTo answersTo = new AnswersTo();
        /*todo nacitani odpovedi*/
        answersTo.setAnswerList(formService.getCurrentAnswerRows().stream().map(this::mapToAnswer).collect(Collectors.toList()));
        return answersTo;
    }

    public void question(AnswersTo answersTo, FlowService.EAction action) {
        if (action.equals(FlowService.EAction.NEXT)) {
            validateService.validate(answersTo, formService.getCurrentAnswerRows());
            /*todo ukladani PI*/
            formService.saveAnswer(answersTo);
        }
        flowService.moveCounterTo(action);
    }

    public CommonTo getCommonTo() {
        final CommonTo commonTo = new CommonTo();
        commonTo.setCompanyName(formService.getCompanyName());
        commonTo.setState(flowService.getFlow().getFlowType().name());
        commonTo.setActions(flowService.getPossibilityActions());
        return commonTo;
    }

    public QuestionsTo getQuestionsTo() {
        final QuestionsTo questionsTo = new QuestionsTo();
        final SubCategory subCategory = formService.getCurrentAnswerRows().get(0).getSubCategory();
        questionsTo.setSubCategory(subCategory.getName());
        questionsTo.setCategory(subCategory.getCategory().getName());
        questionsTo.setQuestionList(formService.getCurrentAnswerRows().stream().map(this::mapToQuestion).collect(Collectors.toList()));
        return questionsTo;
    }

    private AnswerTo mapToAnswer(AbstractAnswerRow answerRow) {
        final AnswerTo answerTo = new AnswerTo();
        answerRow.setNumber(answerRow.getNumber());
        answerRow.setComments(answerRow.getComments());
        return answerTo;
    }

    private QuestionTo mapToQuestion(AbstractAnswerRow answerRow) {
        final QuestionTo questionTo = new QuestionTo();
        questionTo.setNumber(answerRow.getNumber());
        questionTo.setText(answerRow.getQuestion());
        questionTo.setType(answerRow.getClass().getSimpleName());
        return questionTo;
    }

    public void instruction(FlowService.EAction action) {
        flowService.moveCounterTo(action);
    }
}
