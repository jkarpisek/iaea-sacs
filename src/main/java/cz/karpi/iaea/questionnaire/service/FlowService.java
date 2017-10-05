package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import cz.karpi.iaea.questionnaire.model.Flow;
import cz.karpi.iaea.questionnaire.model.SubCategory;

/**
 * Created by karpi on 18.4.17.
 */
@Service
public class FlowService {

    public enum EAction { PREVIOUS, NEXT, FINISH, RESET, SAVE, GOTO, PRINT }

    private Flow flow = new Flow();

    @Autowired
    private FormService formService;

    public Flow getFlow() {
        return flow;
    }

    public void reset() {
        getFlow().resetFlow(Flow.EFlowType.START);
    }

    public void moveCounterTo(EAction action) {
        moveCounterTo(action, null, null, null);
    }

    public void moveCounterTo(EAction action, Flow.EFlowType nextStep, String category, String subCategory) {
        if (action.equals(EAction.GOTO)) {
            moveCounterToPosition(nextStep, category, subCategory);
        } else if (action.equals(EAction.PREVIOUS)) {
            moveCounterToPrevious();
        } else {
            moveCounterToNext();
        }
    }

    public void moveCounterToPosition(Flow.EFlowType nextStep, String category, String subCategory) {
        int categoryIdx;
        int subCategoryIdx;

        if (category.equals("") || subCategory.equals("")) {
            categoryIdx = 0;
            subCategoryIdx = 0;
        } else {
            categoryIdx = IntStream.range(0, formService.getCategories().size())
                    .filter(i -> category.equals(formService.getCategories().get(i).getName()))
                    .findFirst().orElse(-1);

            subCategoryIdx = IntStream.range(0, formService.getCategories().get(categoryIdx).getSubCategories().size())
                    .filter(i -> subCategory.equals(formService.getCategories().get(categoryIdx).getSubCategories().get(i).getName()))
                    .findFirst().orElse(-1);
        }

        getFlow().resetFlow(nextStep);
        getFlow().setCategoryAndSubCategory(categoryIdx, () -> subCategoryIdx);
    }

    private boolean isNotEmpty() {
        return formService.getCategories().get(getFlow().getCurrentCategoryIndex())
                .getSubCategories().get(getFlow().getCurrentSubCategoryIndex())
                .getQuestions().stream().anyMatch(question -> formService.getSacsRows().get(question).getPiGrade() != null);
    }

    public void moveCounterToNext() {
        switch (getFlow().getFlowType()) {
            case START:
                getFlow().resetFlow(Flow.EFlowType.INSTRUCTION);
                break;
            case INSTRUCTION:
                getFlow().resetFlow(Flow.EFlowType.QUESTION);
                break;
            case QUESTION:
                resolveCategoryNext(Flow.EFlowType.ASSESSMENT);
                break;
            case ASSESSMENT:
                //I would try to skip pages which are "empty"
                //check whether next subcategory is not "empty"
                //check wether next category has subcategory which is not "empty"
                //else getFlow().resetFlow(Flow.EFlowType.PLANNER);
                while (formService.getCategories().size() >= getFlow().getCurrentCategoryIndex() + 1) {
                    while (getCurrentSubCategory().getCategory().getSubCategories().size() > getFlow().getCurrentSubCategoryIndex() + 1) {
                        getFlow().upSubCategory();
                        if (isNotEmpty())
                            return;
                    }

                    if (formService.getCategories().size() <= getFlow().getCurrentCategoryIndex() + 1) {
                        getFlow().resetFlow(Flow.EFlowType.PLANNER);
                        return;
                    }

                    getFlow().upCategory();
                    getFlow().resetSubCategory();

                    if (isNotEmpty())
                        return;
                }
                //MS resolveCategoryNext(Flow.EFlowType.PLANNER);
                //break;
            case PLANNER:
                resolveCategoryNext(Flow.EFlowType.CDP);
                break;
        }
    }

    private void resolveCategoryNext(Flow.EFlowType next) {
        if (getCurrentSubCategory().getCategory().getSubCategories().size() > getFlow().getCurrentSubCategoryIndex() + 1) {
            getFlow().upSubCategory();
        } else if (formService.getCategories().size() > getFlow().getCurrentCategoryIndex() + 1){
            getFlow().upCategory();
        } else {
            getFlow().resetFlow(next);
        }
    }

    public void moveCounterToPrevious() {
        switch (getFlow().getFlowType()) {
            case INSTRUCTION:
                getFlow().resetFlow(Flow.EFlowType.START);
                break;
            case QUESTION:
                resolveCategoryPrevious(Flow.EFlowType.INSTRUCTION, 0);
                break;
            case ASSESSMENT:
                resolveCategoryPrevious(Flow.EFlowType.QUESTION, formService.getCategories().size() - 1);
                break;
            case PLANNER:
                resolveCategoryPrevious(Flow.EFlowType.ASSESSMENT, formService.getCategories().size() - 1);
                break;
            case CDP:
                resolveCategoryPrevious(Flow.EFlowType.PLANNER, formService.getCategories().size() - 1);
                break;
        }
    }

    private void resolveCategoryPrevious(Flow.EFlowType previous, Integer categoryIndex) {
        if (getFlow().getCurrentSubCategoryIndex() > 0) {
            getFlow().downSubCategory();
        } else if (getFlow().getCurrentCategoryIndex() > 0){
            getFlow().downCategory(() -> getCurrentSubCategory().getCategory().getSubCategories().size() - 1);
        } else {
            getFlow().resetFlow(previous);
            getFlow().setCategoryAndSubCategory(categoryIndex, () -> getCurrentSubCategory().getCategory().getSubCategories().size() - 1);
        }
    }

    public List<EAction> getPossibilityActions() {
        final List<EAction> actions;
        switch (getFlow().getFlowType()) {
            case START:
                actions = Collections.singletonList(EAction.NEXT);
                break;
            case INSTRUCTION:
                actions = Arrays.asList(EAction.PREVIOUS, EAction.NEXT, EAction.RESET);
                break;
            case QUESTION:
            case ASSESSMENT:
                actions = Arrays.asList(EAction.PREVIOUS, EAction.NEXT, EAction.RESET, EAction.SAVE);
                break;
            case PLANNER:
                actions = !getCurrentPage().equals(getMaxPage())
                          ? Arrays.asList(EAction.PREVIOUS, EAction.NEXT, EAction.RESET, EAction.SAVE)
                          : Arrays.asList(EAction.PREVIOUS, EAction.FINISH, EAction.RESET, EAction.SAVE);
                break;
            case CDP:
                actions = Arrays.asList(EAction.PREVIOUS, EAction.RESET, EAction.SAVE, EAction.PRINT);
                break;
            default:
                actions = Collections.emptyList();
        }
        return actions;
    }

    public Integer getCurrentPage() {
        return formService.sequenceOfSubCategory(getCurrentSubCategory());
    }

    public Integer getMaxPage() {
        return formService.countOfSubCategory();
    }

    public SubCategory getCurrentSubCategory() {
        return formService.getCategories().get(getFlow().getCurrentCategoryIndex()).getSubCategories()
            .get(getFlow().getCurrentSubCategoryIndex());
    }

}
