package cz.karpi.iaea.questionnaire.web.converter;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import cz.karpi.iaea.questionnaire.service.FlowService;
import cz.karpi.iaea.questionnaire.service.to.AnswerTo;
import cz.karpi.iaea.questionnaire.service.to.AnswersTo;
import cz.karpi.iaea.questionnaire.service.to.CommonTo;
import cz.karpi.iaea.questionnaire.service.to.InitTo;
import cz.karpi.iaea.questionnaire.service.to.QuestionTo;
import cz.karpi.iaea.questionnaire.service.to.QuestionsTo;
import cz.karpi.iaea.questionnaire.web.model.AnswerVo;
import cz.karpi.iaea.questionnaire.web.model.AnswersVo;
import cz.karpi.iaea.questionnaire.web.model.CommonVo;
import cz.karpi.iaea.questionnaire.web.model.InitVo;
import cz.karpi.iaea.questionnaire.web.model.QuestionVo;
import cz.karpi.iaea.questionnaire.web.model.QuestionsVo;

/**
 * Created by karpi on 15.4.17.
 */
@Component
public class ViewConverter {

    public AnswersVo toAnswersVo(AnswersTo answersTo) {
        final AnswersVo answersVo = new AnswersVo();
        answersVo.setAnswerList(answersTo.getAnswerList().stream().map(this::mapAnswerVo).collect(Collectors.toList()));
        return answersVo;
    }

    private AnswerVo mapAnswerVo(AnswerTo answerTo) {
        final AnswerVo answerVo = new AnswerVo();
        answerVo.setPiGrade(answerTo.getPiGrade());
        answerVo.setComments(answerTo.getComments());
        return answerVo;
    }

    public AnswersTo toAnswersTo(AnswersVo answersVo) {
        final AnswersTo answersTo = new AnswersTo();
        answersTo.setAnswerList(answersVo.getAnswerList().stream().map(this::mapAnswerTo).collect(Collectors.toList()));
        return answersTo;
    }

    private AnswerTo mapAnswerTo(AnswerVo answerVo) {
        final AnswerTo answerTo = new AnswerTo();
        answerTo.setPiGrade(answerVo.getPiGrade());
        answerTo.setComments(answerVo.getComments());
        return answerTo;
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
        return commonVo;
    }

    public QuestionsVo toQuestionsVo(QuestionsTo questionsTo) {
        final QuestionsVo questionsVo = new QuestionsVo();
        questionsVo.setCategory(questionsTo.getCategory());
        questionsVo.setSubCategory(questionsTo.getSubCategory());
        questionsVo.setQuestionList(questionsTo.getQuestionList().stream().map(this::mapQuestionVo).collect(Collectors.toList()));
        return questionsVo;
    }

    private QuestionVo mapQuestionVo(QuestionTo questionTo) {
        final QuestionVo questionVo = new QuestionVo();
        questionVo.setNumber(questionTo.getNumber());
        questionVo.setText(questionTo.getText());
        questionVo.setType(questionTo.getType());
        return questionVo;
    }

    public FlowService.EAction convertToEAction(String action) {
        return FlowService.EAction.valueOf(action.toUpperCase());
    }

    public Object toAssessmentVo(Object o) {
        return null;
    }

    public Object toPlannerVo(Object o) {
        return null;
    }

    public Object toCdpVo(Object o) {
        return null;
    }
}
