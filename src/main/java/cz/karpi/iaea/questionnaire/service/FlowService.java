package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.karpi.iaea.questionnaire.model.Flow;
import cz.karpi.iaea.questionnaire.model.SubCategory;

/**
 * Created by karpi on 18.4.17.
 */
@Service
public class FlowService {

    public enum EAction { PREVIOUS, NEXT, FINISH }

    private Flow flow = new Flow();

    @Autowired
    private FormService formService;

    public Flow getFlow() {
        return flow;
    }

    public void moveCounterTo(EAction action) {
        if (action.equals(EAction.PREVIOUS)) {
            moveCounterToPrevious();
        } else {
            moveCounterToNext();
        }
    }

    public void moveCounterToNext() {
        switch (getFlow().getFlowType()) {
            case START:
                getFlow().resetFlow(Flow.EFlowType.INSTRUCTION);
                break;
            case INSTRUCTION:
                getFlow().resetFlow(Flow.EFlowType.ASSESSMENT);
                break;
            case QUESTION:
                if (getCurrentSubCategory().getCategory().getSubCategories().size() > getFlow().getCurrentSubCategoryIndex() + 1) {
                    getFlow().upSubCategory();
                } else if (formService.getCategories().size() > getFlow().getCurrentCategoryIndex() + 1){
                    getFlow().upCategory();
                } else {
                    getFlow().resetFlow(Flow.EFlowType.ASSESSMENT);
                }
                break;
            case ASSESSMENT:
                getFlow().resetFlow(Flow.EFlowType.PLANNER);
                break;
            case PLANNER:
                getFlow().resetFlow(Flow.EFlowType.CDP);
                break;
        }
    }

    public void moveCounterToPrevious() {
        switch (getFlow().getFlowType()) {
            case INSTRUCTION:
                getFlow().resetFlow(Flow.EFlowType.START);
                break;
            case QUESTION:
                if (getFlow().getCurrentSubCategoryIndex() > 0) {
                    getFlow().downSubCategory();
                } else if (getFlow().getCurrentCategoryIndex() > 0){
                    getFlow().downCategory(getCurrentSubCategory().getCategory().getSubCategories().size() - 1);
                } else {
                    getFlow().resetFlow(Flow.EFlowType.INSTRUCTION);
                }
                break;
            case ASSESSMENT:
                getFlow().resetFlow(Flow.EFlowType.QUESTION);
                getFlow().setCategoryAndSubCategory(formService.getCategories().size() - 1, getCurrentSubCategory().getCategory().getSubCategories().size() - 1);
                break;
            case PLANNER:
                getFlow().resetFlow(Flow.EFlowType.ASSESSMENT);
                getFlow().setCategoryAndSubCategory(formService.getCategories().size() - 1, getCurrentSubCategory().getCategory().getSubCategories().size() - 1);
                break;
            case CDP:
                getFlow().resetFlow(Flow.EFlowType.PLANNER);
                getFlow().setCategoryAndSubCategory(formService.getCategories().size() - 1, getCurrentSubCategory().getCategory().getSubCategories().size() - 1);
                break;
        }
    }

    public List<EAction> getPossibilityActions() {
        final List<EAction> actions;
        switch (getFlow().getFlowType()) {
            case START:
                actions = Collections.singletonList(EAction.NEXT);
                break;
            case INSTRUCTION:
            case QUESTION:
            case ASSESSMENT:
                actions = Arrays.asList(EAction.PREVIOUS, EAction.NEXT);
                break;
            case PLANNER:
                actions = Arrays.asList(EAction.PREVIOUS, EAction.FINISH);
                break;
            case CDP:
                actions = Collections.singletonList(EAction.PREVIOUS);
                break;
            default:
                actions = Collections.emptyList();
        }
        return actions;
    }

    public Integer getCurrentPage() {
        return getFlow().getCurrentSubCategoryIndex();
    }

    public Integer getMaxPage() {
        return getFlow().getMaxSubCategoryIndex();
    }

    public SubCategory getCurrentSubCategory() {
        return formService.getCategories().get(getFlow().getCurrentCategoryIndex()).getSubCategories()
            .get(getFlow().getCurrentSubCategoryIndex());
    }

}
