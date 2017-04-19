package cz.karpi.iaea.questionnaire.web.model;

import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class CommonVo {
    private String companyName;
    private List<String> actions;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }
}
