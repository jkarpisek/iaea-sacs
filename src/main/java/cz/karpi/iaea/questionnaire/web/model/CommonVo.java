package cz.karpi.iaea.questionnaire.web.model;

import cz.karpi.iaea.questionnaire.model.Category;
import cz.karpi.iaea.questionnaire.model.MenuEntry;

import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class CommonVo {
    private String companyName;
    private String intro;
    private List<String> actions;
    private List<Category> categories;
    private List<MenuEntry> questionnaireMenu;
    private List<MenuEntry> assessmentMenu;
    private List<MenuEntry> plannerMenu;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIntro() { return intro; }

    public void setIntro(String intro) { this.intro = intro; }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
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
