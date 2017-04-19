package cz.karpi.iaea.questionnaire.service;

import org.springframework.stereotype.Service;

import cz.karpi.iaea.questionnaire.service.exception.ValidationException;
import cz.karpi.iaea.questionnaire.service.to.AnswerTo;
import cz.karpi.iaea.questionnaire.service.to.AnswersTo;
import cz.karpi.iaea.questionnaire.service.to.InitTo;

/**
 * Created by karpi on 18.4.17.
 */
@Service
public class ValidateService {

    private static final Integer MIN_PI_GRADE = 1;
    private static final Integer MAX_PI_GRADE = 4;

    public void validate(InitTo initTo) {
        if (initTo.getCompanyName() == null || initTo.getCompanyName().isEmpty()) {
            throw new ValidationException();
        }
    }

    public void validate(AnswersTo answersTo) {
        if (answersTo.getAnswerList().stream().anyMatch(this::validate)) {
            throw new ValidationException();
        }
    }

    private boolean validate(AnswerTo answerTo) {
        //todo not validate additional comments
        return answerTo.getComments().isEmpty() || answerTo.getPiGrade() < MIN_PI_GRADE || answerTo.getPiGrade() > MAX_PI_GRADE;
    }

    
}
