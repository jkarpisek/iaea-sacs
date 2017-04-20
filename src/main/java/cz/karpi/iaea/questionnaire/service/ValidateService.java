package cz.karpi.iaea.questionnaire.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

import cz.karpi.iaea.questionnaire.model.AbstractAnswerRow;
import cz.karpi.iaea.questionnaire.model.AnswerWithPiGradeRow;
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

    public void validate(InitTo initTo) {
        if (initTo.getCompanyName() == null || initTo.getCompanyName().isEmpty()) {
            throw new ValidationException();
        }
    }

    public void validate(AnswersTo answersTo, List<AbstractAnswerRow> currentAnswerRows) {
        if (answersTo.getAnswerList().size() != currentAnswerRows.size()) {
            throw new ValidationException();
        }
        if (IntStream.range(0, currentAnswerRows.size()).anyMatch(i -> validate(answersTo.getAnswerList().get(i), currentAnswerRows.get(i)))) {
            throw new ValidationException();
        }
    }

    private boolean validate(AnswerTo answerTo, AbstractAnswerRow row) {
        return row.getClass().isAssignableFrom(AnswerWithPiGradeRow.class)
               && (answerTo.getComments().isEmpty() || answerTo.getPiGrade() < MIN_PI_GRADE || answerTo.getPiGrade() > MAX_PI_GRADE);
    }

    
}
