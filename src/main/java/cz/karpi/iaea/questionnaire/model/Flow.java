package cz.karpi.iaea.questionnaire.model;

import java.util.function.Supplier;

/**
 * Created by karpi on 17.4.17.
 */
public class Flow {
    public enum EFlowType { START, INSTRUCTION, QUESTION, ASSESSMENT, PLANNER, CDP }
    private EFlowType flowType;
    private Integer currentCategoryIndex;
    private Integer currentSubCategoryIndex;

    public Flow() {
        resetFlow(EFlowType.START);
    }

    public EFlowType getFlowType() {
        return flowType;
    }

    public Integer getCurrentCategoryIndex() {
        return currentCategoryIndex;
    }

    public Integer getCurrentSubCategoryIndex() {
        return currentSubCategoryIndex;
    }

    public void resetFlow(Flow.EFlowType flowType) {
        this.flowType = flowType;
        resetCategory();
    }

    public void resetCategory() {
        currentCategoryIndex = 0;
        resetSubCategory();
    }

    public void upCategory() {
        currentCategoryIndex = getCurrentCategoryIndex() + 1;
        resetSubCategory();
    }

    public void downCategory(Supplier<Integer> subCategory) {
        currentCategoryIndex = getCurrentCategoryIndex() - 1;
        currentSubCategoryIndex = subCategory.get();
    }

    public void resetSubCategory() {
        currentSubCategoryIndex =  0;
    }

    public void upSubCategory() {
        currentSubCategoryIndex = getCurrentSubCategoryIndex() + 1;
    }

    public void downSubCategory() {
        currentSubCategoryIndex = getCurrentSubCategoryIndex() - 1;
    }

    public void setCategoryAndSubCategory(Integer category, Supplier<Integer> subCategory) {
        currentCategoryIndex = category;
        currentSubCategoryIndex = subCategory.get();
    }

    @Override
    public String toString() {
        return "Flow{" +
               "flowType=" + flowType +
               ", currentCategoryIndex=" + currentCategoryIndex +
               ", currentSubCategoryIndex=" + currentSubCategoryIndex +
               '}';
    }
}
