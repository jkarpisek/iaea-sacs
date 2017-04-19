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
        return answerTo.getComments().isEmpty() || answerTo.getPiGrade() < 1 || answerTo.getPiGrade() > 4;
    }

    
}
