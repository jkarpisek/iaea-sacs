package cz.karpi.iaea.questionnaire.model;

/**
 * Created by karpi on 16.5.17.
 */
public class Quarter {
    private Integer name;
    private Year year;

    public Quarter(Integer name, Year year) {
        this.name = name;
        this.year = year;
    }

    public Integer getName() {
        return name;
    }

    public Year getYear() {
        return year;
    }
}
