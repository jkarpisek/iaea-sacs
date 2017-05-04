package cz.karpi.iaea.questionnaire.model;

/**
 * Created by karpi on 17.4.17.
 */
public class Flow {
    public enum EFlowType { START, INSTRUCTION, QUESTION, ASSESSMENT, PLANNER, CDP }
    private EFlowType flowType;
    private Integer currentIndex;
    private Integer currentSubIndex;

    public Flow() {
        this.flowType = EFlowType.START;
        this.currentIndex = 0;
        this.currentSubIndex = 0;
    }

    public EFlowType getFlowType() {
        return flowType;
    }

    public void setFlowType(EFlowType flowType) {
        this.flowType = flowType;
    }

    public Integer getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(Integer currentIndex) {
        this.currentIndex = currentIndex;
    }

    public Integer getCurrentSubIndex() {
        return currentSubIndex;
    }

    public void setCurrentSubIndex(Integer currentSubIndex) {
        this.currentSubIndex = currentSubIndex;
    }
}
