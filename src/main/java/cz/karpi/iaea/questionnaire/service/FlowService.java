package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cz.karpi.iaea.questionnaire.model.Flow;

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
        switch (flow.getFlowType()) {
            case START:
                flow.setFlowType(Flow.EFlowType.INSTRUCTION);
                flow.setCurrentIndex(0);
                flow.setCurrentSubIndex(0);
                break;
            case INSTRUCTION:
                flow.setFlowType(Flow.EFlowType.QUESTION);
                flow.setCurrentIndex(0);
                flow.setCurrentSubIndex(0);
                break;
            case QUESTION:
                if (formService.getSacsForm().getCategories().get(flow.getCurrentIndex()).getSubCategories().size() > flow.getCurrentSubIndex() + 1) {
                    flow.setCurrentSubIndex(flow.getCurrentSubIndex() + 1);
                } else if (formService.getSacsForm().getCategories().size() > flow.getCurrentIndex() + 1){
                    flow.setCurrentIndex(flow.getCurrentIndex() + 1);
                    flow.setCurrentSubIndex(0);
                } else {
                    flow.setFlowType(Flow.EFlowType.FINISH);
                    flow.setCurrentIndex(0);
                    flow.setCurrentSubIndex(0);
                }
                break;
        }
    }

    public void moveCounterToPrevious() {
        switch (flow.getFlowType()) {
            case INSTRUCTION:
                flow.setFlowType(Flow.EFlowType.START);
                flow.setCurrentIndex(0);
                flow.setCurrentSubIndex(0);
                break;
            case QUESTION:
                if (flow.getCurrentSubIndex() > 0) {
                    flow.setCurrentSubIndex(flow.getCurrentSubIndex() - 1);
                } else if (flow.getCurrentIndex() > 0){
                    flow.setCurrentIndex(flow.getCurrentIndex() - 1);
                    flow.setCurrentSubIndex(formService.getSacsForm().getCategories().get(flow.getCurrentIndex()).getSubCategories().size() - 1);
                } else {
                    flow.setFlowType(Flow.EFlowType.INSTRUCTION);
                    flow.setCurrentIndex(0);
                    flow.setCurrentSubIndex(0);
                }
                break;
            case FINISH:
                flow.setFlowType(Flow.EFlowType.QUESTION);
                flow.setCurrentIndex(formService.getSacsForm().getCategories().size() - 1);
                flow.setCurrentSubIndex(formService.getSacsForm().getCategories().get(flow.getCurrentIndex()).getSubCategories().size() - 1);
                break;
        }
    }

    public List<EAction> getPossibilityActions() {
        final List<EAction> actions;
        switch (flow.getFlowType()) {
            case START:
                actions = Collections.singletonList(EAction.NEXT);
                break;
            case INSTRUCTION:
                actions = Arrays.asList(EAction.PREVIOUS, EAction.NEXT);
                break;
            case QUESTION:
                actions = new ArrayList<>();
                actions.add(EAction.PREVIOUS);
                if (formService.getSacsForm().getCategories().get(flow.getCurrentIndex()).getSubCategories().size() > flow.getCurrentSubIndex() + 1
                    || formService.getSacsForm().getCategories().size() > flow.getCurrentIndex() + 1){
                    actions.add(EAction.NEXT);
                } else {
                    actions.add(EAction.FINISH);
                }
                break;
            default:
                actions = Collections.emptyList();
        }
        return actions;
    }

}
