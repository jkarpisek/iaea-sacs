package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cz.karpi.iaea.questionnaire.model.AnswerRow;
import cz.karpi.iaea.questionnaire.model.AssessmentRow;
import cz.karpi.iaea.questionnaire.model.Category;
import cz.karpi.iaea.questionnaire.model.EQuestionType;
import cz.karpi.iaea.questionnaire.model.Element;
import cz.karpi.iaea.questionnaire.model.PlannerRow;
import cz.karpi.iaea.questionnaire.model.Quarter;
import cz.karpi.iaea.questionnaire.model.Question;
import cz.karpi.iaea.questionnaire.model.SubCategory;
import cz.karpi.iaea.questionnaire.model.Year;
import cz.karpi.iaea.questionnaire.persistence.FormDao;
import cz.karpi.iaea.questionnaire.service.to.AnswersTo;
import cz.karpi.iaea.questionnaire.service.to.AssessmentAnswersTo;
import cz.karpi.iaea.questionnaire.service.to.InitTo;
import cz.karpi.iaea.questionnaire.service.to.PlannerAnswersTo;

/**
 * Created by karpi on 12.4.17.
 */
@Service
public class FormService {

    private String companyName;

    private List<Category> categories;

    private List<Element> elements;

    private List<Year> years;

    @Autowired
    private FormDao formDao;

    public void saveAnswer(AnswersTo answersTo) {
        final List<AnswerRow> answers = answersTo.getAnswerList().stream().map(answerTo -> {
            final Question question = getQuestion(answerTo.getNumber());
            final AnswerRow answerRow = new AnswerRow();
            answerRow.setQuestion(question);
            answerRow.setComments(answerTo.getComments());
            if (question.getType().equals(EQuestionType.WITH_PIGRADE)) {
                answerRow.setPiGrade(answerTo.getPiGrade());
            }
            return answerRow;
        }).collect(Collectors.toList());

        formDao.saveForm(getCompanyName(), answers);
    }

    public void saveInit(InitTo initTo) {
        this.companyName = initTo.getCompanyName();
    }

    public void loadDefinitions() {
        formDao.copyForm(companyName);
        categories = formDao.getSACSDefinition(companyName);
        elements = formDao.getAssessmentDefinition(companyName);
        years = formDao.getPlannerDefinition(companyName, categories.get(0).getSubCategories().get(0).getQuestions().get(0).getNumber(), generateYears());
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Element> getElements() {
        return elements;
    }

    public List<Year> getYears() {
        return years;
    }

    private List<Year> generateYears() {
        final Integer startYear = Calendar.getInstance().get(Calendar.YEAR);
        return IntStream.rangeClosed(0, 4).mapToObj(yearIndex -> {
            final Year year = new Year(startYear + yearIndex);
            year.setQuarters(IntStream.rangeClosed(1, 4).mapToObj(quarterIndex -> new Quarter(quarterIndex, year))
                                 .collect(Collectors.toList()));
            return year;
        }).collect(Collectors.toList());
    }

    public Question getQuestion(String number) {
        return getCategories().stream().flatMap(category -> category.getSubCategories().stream())
            .flatMap(subCategory -> subCategory.getQuestions().stream()).filter(question -> question.getNumber().equals(number)).findFirst().orElse(null);
    }

    public Integer sequenceOfSubCategory(SubCategory subCategory) {
        return 1 + getCategories().stream().flatMap(category -> category.getSubCategories().stream()).collect(Collectors.toList()).indexOf(subCategory);
    }

    public Integer countOfSubCategory() {
        return getCategories().stream().mapToInt(category -> category.getSubCategories().size()).sum();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void saveAssessmentAnswer(AssessmentAnswersTo assessmentAnswersTo) {
        final List<AssessmentRow> answers = assessmentAnswersTo.getAnswerList().stream().map(assessmentAnswerTo -> {
            final AssessmentRow assessmentRow = new AssessmentRow();
            assessmentRow.setQuestion(getQuestion(assessmentAnswerTo.getNumber()));
            assessmentRow.setElementsPiGrade(assessmentAnswerTo.getPiGrades());
            return assessmentRow;
        }).collect(Collectors.toList());
        formDao.saveAssessmentAnswers(getCompanyName(), answers);
    }

    public void savePlannerAnswer(PlannerAnswersTo plannerAnswersTo) {
        final List<PlannerRow> answers = plannerAnswersTo.getAnswerList().stream().map(plannerAnswerTo -> {
            final PlannerRow plannerRow = new PlannerRow();
            plannerRow.setQuestion(getQuestion(plannerAnswerTo.getNumber()));
            plannerRow.setElement(plannerAnswerTo.getElement());
            plannerRow.setOwnership(plannerAnswerTo.getOwnership());
            plannerRow.setTask(plannerAnswerTo.getTask());
            plannerRow.setPlanned(plannerAnswerTo.getPlanned());
            return plannerRow;
        }).collect(Collectors.toList());
        formDao.savePlannerAnswers(getCompanyName(), answers);
    }
}
