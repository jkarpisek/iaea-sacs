package cz.karpi.iaea.questionnaire.service.exception;

/**
 * Created by karpi on 13.4.17.
 */
public class ValidationException extends RuntimeException {
    //todo not object
    private Object errors;

    public ValidationException() {
        this(null);
    }

    public ValidationException(Object errors) {
        this.errors = errors;
    }

    public Object getErrors() {
        return errors;
    }
}
