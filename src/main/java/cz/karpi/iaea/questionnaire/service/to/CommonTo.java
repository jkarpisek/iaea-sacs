package cz.karpi.iaea.questionnaire.service.to;

import java.util.ArrayList;
import java.util.List;

import cz.karpi.iaea.questionnaire.service.FlowService;

/**
 * Created by karpi on 18.4.17.
 */
public class CommonTo {
    private String companyName;
    private String state;
    private List<FlowService.EAction> actions;

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

    public List<FlowService.EAction> getActions() {
        if (actions == null) {
            actions = new ArrayList<>();
        }
        return actions;
    }

    public void setActions(List<FlowService.EAction> actions) {
        this.actions = actions;
    }
}
