package cz.karpi.iaea.questionnaire.model;

/**
 * Created by karpi on 2.5.17.
 */
public class Element {
    private String name;

    public Element(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Element{" +
               "name='" + name + '\'' +
               '}';
    }
}
