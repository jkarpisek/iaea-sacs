package cz.karpi.iaea.questionnaire.service.to;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karpi on 18.4.17.
 */
public class CommonTo {
    private String companyName;
    private String state;
    private List<String> actions;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<String> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }
}
