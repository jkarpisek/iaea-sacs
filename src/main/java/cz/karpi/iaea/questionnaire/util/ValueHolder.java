package cz.karpi.iaea.questionnaire.util;

/**
 * Created by karpi on 19.4.17.
 */
public class ValueHolder<T> {
    private T value;

    public ValueHolder(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
