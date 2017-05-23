package cz.karpi.iaea.questionnaire.model;

/**
 * Created by karpi on 17.4.17.
 */
public class Flow {
    public enum EFlowType { START, INSTRUCTION, QUESTION, ASSESSMENT, PLANNER, CDP }
    private EFlowType flowType;
    private Integer currentCategoryIndex;
    private Integer currentSubCategoryIndex;
    private Integer maxSubCategoryIndex;

    public Flow() {
        resetFlow(EFlowType.START);
        this.maxSubCategoryIndex = 0;
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

    public Integer getMaxSubCategoryIndex() {
        return maxSubCategoryIndex;
    }

    public void setMaxSubCategoryIndex(Integer maxSubCategoryIndex) {
        this.maxSubCategoryIndex = maxSubCategoryIndex;
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

    public void downCategory(Integer subCategory) {
        currentCategoryIndex = getCurrentCategoryIndex() - 1;
        currentSubCategoryIndex = subCategory;
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

    public void setCategoryAndSubCategory(Integer category, Integer subCategory) {
        currentCategoryIndex = category;
        currentSubCategoryIndex = subCategory;
    }

}
