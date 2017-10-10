package cz.karpi.iaea.questionnaire.service;

import cz.karpi.iaea.questionnaire.model.Element;
import cz.karpi.iaea.questionnaire.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import cz.karpi.iaea.questionnaire.model.Flow;
import cz.karpi.iaea.questionnaire.model.SubCategory;

import static cz.karpi.iaea.questionnaire.model.Flow.EFlowType.ASSESSMENT;
import static cz.karpi.iaea.questionnaire.model.Flow.EFlowType.PLANNER;

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

        if (category == null || subCategory == null || category.equals("") || subCategory.equals("")) {
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

    private boolean isNotEmpty(Flow.EFlowType currentFlow) {
        if (currentFlow == Flow.EFlowType.ASSESSMENT) {
            return formService.getCategories().get(getFlow().getCurrentCategoryIndex())
                    .getSubCategories().get(getFlow().getCurrentSubCategoryIndex())
                    .getQuestions().stream().anyMatch(question -> formService.getSacsRows().get(question).getPiGrade() != null);
        } else if (currentFlow == Flow.EFlowType.PLANNER) {
            List<Question> questions = formService
                    .getCategories()
                    .get(getFlow().getCurrentCategoryIndex())
                    .getSubCategories()
                    .get(getFlow().getCurrentSubCategoryIndex())
                    .getQuestions();
            for (Question question: questions) {
                for (Map.Entry<Element, Integer> piGrade : formService.getAssessmentRow(question).getElementsPiGrade().entrySet()) {
                    if (piGrade.getValue() != null) return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    private void setFlowToNextNonEmptySection(Flow.EFlowType currentFlow) {
        //skip pages which are "empty"
        //check whether next subcategory is not "empty"
        //check whether next category has subcategory which is not "empty"
        //else getFlow().resetFlow(Flow.EFlowType.PLANNER);
        while (formService.getCategories().size() >= getFlow().getCurrentCategoryIndex() + 1) {
            while (getCurrentSubCategory().getCategory().getSubCategories().size() > getFlow().getCurrentSubCategoryIndex() + 1) {
                getFlow().upSubCategory();
                if (isNotEmpty(currentFlow))
                    return;
            }

            if (formService.getCategories().size() <= getFlow().getCurrentCategoryIndex() + 1) {
                if (currentFlow == Flow.EFlowType.ASSESSMENT) {
                    getFlow().resetFlow(Flow.EFlowType.PLANNER);
                    if (isNotEmpty(currentFlow))
                        return;

                    //find next non-empty planner
                    setFlowToNextNonEmptySection(Flow.EFlowType.PLANNER);
                } else if (currentFlow == Flow.EFlowType.PLANNER) {
                    getFlow().resetFlow(Flow.EFlowType.CDP);
                }
                return;
            }

            getFlow().upCategory();
            getFlow().resetSubCategory();

            if (isNotEmpty(currentFlow))
                return;
        }
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
                if (getCurrentSubCategory().getCategory().getSubCategories().size() > getFlow().getCurrentSubCategoryIndex() + 1) {
                    getFlow().upSubCategory();
                } else if (formService.getCategories().size() > getFlow().getCurrentCategoryIndex() + 1){
                    getFlow().upCategory();
                } else {
                    getFlow().resetFlow(Flow.EFlowType.ASSESSMENT);
                    if (isNotEmpty(Flow.EFlowType.ASSESSMENT))
                        return;
                    setFlowToNextNonEmptySection(Flow.EFlowType.ASSESSMENT);
                }
                break;
            case ASSESSMENT:
                setFlowToNextNonEmptySection(Flow.EFlowType.ASSESSMENT);
                break;
            case PLANNER:
                setFlowToNextNonEmptySection(Flow.EFlowType.PLANNER);
                break;
        }
    }

    private void setFlowToPreviousNonEmptySection(Flow.EFlowType currentFlow) {
        while (getFlow().getCurrentCategoryIndex() >= 0) {
            while (getFlow().getCurrentSubCategoryIndex() - 1 >= 0) {
                getFlow().downSubCategory();
                if (isNotEmpty(currentFlow))
                    return;
            }

            if (getFlow().getCurrentCategoryIndex() - 1 < 0) {
                if (currentFlow == Flow.EFlowType.CDP) {
                    getFlow().resetFlow(Flow.EFlowType.PLANNER);
                    getFlow().setCategoryAndSubCategory(
                            formService.getCategories().size() - 1,
                            () -> formService.getCategories().get(formService.getCategories().size() - 1).getSubCategories().size() - 1
                    );
                    setFlowToPreviousNonEmptySection(Flow.EFlowType.PLANNER);
                }
                if (currentFlow == Flow.EFlowType.PLANNER) {
                    getFlow().resetFlow(Flow.EFlowType.ASSESSMENT);
                    getFlow().setCategoryAndSubCategory(
                            formService.getCategories().size() - 1,
                            () -> formService.getCategories().get(formService.getCategories().size() - 1).getSubCategories().size() - 1
                    );
                    //find next non-empty planner
                    setFlowToPreviousNonEmptySection(Flow.EFlowType.ASSESSMENT);
                } else if (currentFlow == Flow.EFlowType.ASSESSMENT) {
                    getFlow().resetFlow(Flow.EFlowType.QUESTION);
                    getFlow().setCategoryAndSubCategory(
                            formService.getCategories().size() - 1,
                            () -> formService.getCategories().get(formService.getCategories().size() - 1).getSubCategories().size() - 1
                    );
                }
                return;
            }

            getFlow().downCategory(() -> getCurrentSubCategory().getCategory().getSubCategories().size() - 1);
            getFlow().setCategoryAndSubCategory(
                    getFlow().getCurrentCategoryIndex(),
                    () -> formService.getCategories().get(getFlow().getCurrentCategoryIndex()).getSubCategories().size() - 1
            );

            if (isNotEmpty(currentFlow))
                return;
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
                    getFlow().downCategory(() -> getCurrentSubCategory().getCategory().getSubCategories().size() - 1);
                } else {
                    getFlow().resetFlow(Flow.EFlowType.INSTRUCTION);
                }
                break;
            case ASSESSMENT:
                //find previous non empty or jump to the last question
                setFlowToPreviousNonEmptySection(Flow.EFlowType.ASSESSMENT);
                break;
            case PLANNER:
                setFlowToPreviousNonEmptySection(Flow.EFlowType.PLANNER);
                break;
            case CDP:
                setFlowToPreviousNonEmptySection(Flow.EFlowType.CDP);
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
