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

/**
 * Created by karpi on 15.4.17.
 */
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
        answersTo.setAnswerList(formService.getCurrentAnswerRows().stream().map(this::ddd).collect(Collectors.toList()));
        return answersTo;
    }

    public void question(AnswersTo answersTo, String action) {
        if (action.equals("next")) {
            validateService.validate(answersTo);
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
        questionsTo.setQuestionList(formService.getCurrentAnswerRows().stream().map(this::dd).collect(Collectors.toList()));
        return questionsTo;
    }

    private AnswerTo ddd(AbstractAnswerRow answerRow) {
        final AnswerTo answerTo = new AnswerTo();
        answerRow.setNumber(answerRow.getNumber());
        answerRow.setComments(answerRow.getComments());
        return answerTo;
    }

    private QuestionTo dd(AbstractAnswerRow answerRow) {
        final QuestionTo questionTo = new QuestionTo();
        questionTo.setNumber(answerRow.getNumber());
        questionTo.setText(answerRow.getQuestion());
        return questionTo;
    }

    public void instruction(String action) {
        flowService.moveCounterTo(action);
    }
}
