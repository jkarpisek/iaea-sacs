package cz.karpi.iaea.questionnaire.service.to;

import java.util.Map;

import cz.karpi.iaea.questionnaire.model.Element;
import cz.karpi.iaea.questionnaire.model.Quarter;

/**
 * Created by karpi on 15.4.17.
 */
public class PlannerAnswerTo {
    private String number;
    private Element element;
    private String task;
    private String ownership;
    private Map<Quarter, Boolean> planned;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public Map<Quarter, Boolean> getPlanned() {
        return planned;
    }

    public void setPlanned(Map<Quarter, Boolean> planned) {
        this.planned = planned;
    }
}
