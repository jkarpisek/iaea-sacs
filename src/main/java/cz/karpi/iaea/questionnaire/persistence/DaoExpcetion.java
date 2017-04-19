package cz.karpi.iaea.questionnaire.persistence;

/**
 * Created by karpi on 12.4.17.
 */
public class DaoExpcetion extends RuntimeException {
    public DaoExpcetion(String message, Throwable cause) {
        super(message, cause);
    }
}
