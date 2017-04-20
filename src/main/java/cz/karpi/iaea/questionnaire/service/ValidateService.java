package cz.karpi.iaea.questionnaire.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        final List<Object[]> errors = IntStream.range(0, currentAnswerRows.size())
            .mapToObj(i -> validate(i, answersTo.getAnswerList().get(i), currentAnswerRows.get(i)))
            .filter(list -> !list.isEmpty()).flatMap(List::stream).collect(Collectors.toList());
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private List<Object[]> validate(Integer index, AnswerTo answerTo, AbstractAnswerRow row) {
        final List<Object[]> errors = new ArrayList<>();
        if (row.getClass().isAssignableFrom(AnswerWithPiGradeRow.class)) {
            if (answerTo.getComments().isEmpty()) {
                errors.add(new Object[] { index, "comments" });
            }
            if (answerTo.getPiGrade() == null || answerTo.getPiGrade() < MIN_PI_GRADE || answerTo.getPiGrade() > MAX_PI_GRADE) {
                errors.add(new Object[] { index, "piGrade" });
            }
        }
        return errors;
    }

    
}
