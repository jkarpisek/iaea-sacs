package cz.karpi.iaea.questionnaire.service.to;

import java.util.ArrayList;
import java.util.List;

import cz.karpi.iaea.questionnaire.model.Category;
import cz.karpi.iaea.questionnaire.model.MenuEntry;
import cz.karpi.iaea.questionnaire.service.FlowService;

/**
 * Created by karpi on 18.4.17.
 */
public class CommonTo {
    private String companyName;
    private String state;
    private List<FlowService.EAction> actions;
    private List<Category> categories;//MS TODO DELETE??? Of course not only here!
    private List<MenuEntry> questionnaireMenu;
    private List<MenuEntry> assessmentMenu;
    private List<MenuEntry> plannerMenu;

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

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<MenuEntry> getQuestionnaireMenu() {
        return questionnaireMenu;
    }

    public void setQuestionnaireMenu(List<MenuEntry> questionnaireMenu) {
        this.questionnaireMenu = questionnaireMenu;
    }

    public List<MenuEntry> getAssessmentMenu() {
        return assessmentMenu;
    }

    public void setAssessmentMenu(List<MenuEntry> assessmentMenu) {
        this.assessmentMenu = assessmentMenu;
    }

    public List<MenuEntry> getPlannerMenu() {
        return plannerMenu;
    }

    public void setPlannerMenu(List<MenuEntry> plannerMenu) {
        this.plannerMenu = plannerMenu;
    }
}
