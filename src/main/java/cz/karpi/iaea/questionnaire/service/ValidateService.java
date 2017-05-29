package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cz.karpi.iaea.questionnaire.model.EQuestionType;
import cz.karpi.iaea.questionnaire.model.Question;
import cz.karpi.iaea.questionnaire.service.exception.ValidationException;
import cz.karpi.iaea.questionnaire.service.to.AnswerTo;
import cz.karpi.iaea.questionnaire.service.to.AnswersTo;
import cz.karpi.iaea.questionnaire.service.to.InitTo;

/**
 * Created by karpi on 18.4.17.
 */
@Service
public class ValidateService {

    private static final Integer MIN_PI_GRADE = 0;
    private static final Integer MAX_PI_GRADE = 3;

    @Autowired
    private FormService formService;

    public void validate(InitTo initTo) {
        if (initTo.getCompanyName() == null || initTo.getCompanyName().isEmpty()) {
            throw new ValidationException(Collections.emptyList());
        }
    }

    public void validate(AnswersTo answersTo) {
        final List<Object[]> errors = answersTo.getAnswerList().stream()
            .map(answerTo -> validate(answerTo, formService.getQuestion(answerTo.getNumber())))
            .filter(list -> !list.isEmpty()).flatMap(List::stream).collect(Collectors.toList());
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private List<Object[]> validate(AnswerTo answerTo, Question row) {
        final List<Object[]> errors = new ArrayList<>();
        if (row.getType().equals(EQuestionType.WITH_PIGRADE)) {
            if (answerTo.getComments().isEmpty()) {
                //errors.add(new Object[] { answerTo.getNumber(), "comments" });
            }
            if (answerTo.getPiGrade() == null || answerTo.getPiGrade() < MIN_PI_GRADE || answerTo.getPiGrade() > MAX_PI_GRADE) {
                //errors.add(new Object[] { answerTo.getNumber(), "piGrade" });
            }
        }
        return errors;
    }

    
}
