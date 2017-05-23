package cz.karpi.iaea.questionnaire.service.to;

import java.util.List;

/**
 * Created by karpi on 15.4.17.
 */
public class AssessmentTo extends AbstractTo {
    private List<ElementTo> elements;
    private List<AssessmentQuestionTo> assessmentQuestions;

    public List<ElementTo> getElements() {
        return elements;
    }

    public void setElements(List<ElementTo> elements) {
        this.elements = elements;
    }

    public List<AssessmentQuestionTo> getAssessmentQuestions() {
        return assessmentQuestions;
    }

    public void setAssessmentQuestions(List<AssessmentQuestionTo> assessmentQuestions) {
        this.assessmentQuestions = assessmentQuestions;
    }
}
