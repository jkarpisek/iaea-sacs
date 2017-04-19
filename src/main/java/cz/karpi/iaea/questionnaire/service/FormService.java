package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import cz.karpi.iaea.questionnaire.model.AbstractAnswerRow;
import cz.karpi.iaea.questionnaire.model.SACSForm;
import cz.karpi.iaea.questionnaire.persistence.FormDao;
import cz.karpi.iaea.questionnaire.service.to.AnswerTo;
import cz.karpi.iaea.questionnaire.service.to.AnswersTo;
import cz.karpi.iaea.questionnaire.service.to.InitTo;
import cz.karpi.iaea.questionnaire.util.ValueHolder;

/**
 * Created by karpi on 12.4.17.
 */
@Service
public class FormService {

    private String companyName;

    private SACSForm sacsForm;

    @Autowired
    private FormDao formDao;

    @Autowired
    private FlowService flowService;

    public SACSForm getSacsForm() {
        return sacsForm;
    }

    public void saveAnswer(AnswersTo answers) {
        final ValueHolder<Integer> index = new ValueHolder<>(0);
        getCurrentAnswerRows().forEach(answerRow -> {
            final AnswerTo answerTo = answers.getAnswerList().get(index.getValue());
            answerRow.setComments(answerTo.getComments());
            index.setValue(index.getValue() + 1);
        });

        formDao.saveForm(getCompanyName(), getCurrentAnswerRows());
    }

    public void saveInit(InitTo initTo) {
        this.companyName = initTo.getCompanyName();
    }

    public void loadSACSForm() {
        formDao.copyForm(companyName);
        sacsForm = formDao.getSACSForm(companyName);
    }

    public List<AbstractAnswerRow> getCurrentAnswerRows() {
        return sacsForm.getCategories().get(flowService.getFlow().getCurrentIndex()).getSubCategories()
            .get(flowService.getFlow().getCurrentSubIndex()).getAnswerRows();
    }

    public String getCompanyName() {
        return companyName;
    }
}
