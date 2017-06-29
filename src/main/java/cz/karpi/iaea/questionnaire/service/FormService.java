package cz.karpi.iaea.questionnaire.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import cz.karpi.iaea.questionnaire.model.AnswerRow;
import cz.karpi.iaea.questionnaire.model.AssessmentRow;
import cz.karpi.iaea.questionnaire.model.Category;
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

    private Map<Question, AnswerRow> sacsRows;

    private Map<Question, AssessmentRow> assessmentRows;

    private Map<Question, Map<Element, PlannerRow>> plannerRows;

    @Autowired
    private FormDao formDao;

    public void reset() {
        companyName = null;
        categories = null;
        sacsRows = null;
        elements = null;
        assessmentRows = null;
        years = null;
        plannerRows = null;
        formDao.reset();
    }

    public void loadDefinitions() {
        formDao.copyForm(companyName);
        categories = formDao.getSACSDefinition();
        sacsRows = formDao.getSACSAnswers(categories);
        elements = formDao.getAssessmentDefinition();
        assessmentRows = formDao.getAssessmentAnswers(categories, elements);
        years = formDao.getPlannerDefinition(categories, generateYears());
        plannerRows = formDao.getPlannerAnswers(categories, elements, years);
    }

    public void saveInit(InitTo initTo) {
        this.companyName = initTo.getCompanyName();
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

    public void saveAnswer(AnswersTo answersTo) {
        final List<AnswerRow> answers = answersTo.getAnswerList().stream().map(answerTo -> {
            final Question question = getQuestion(answerTo.getNumber());
            final AnswerRow answerRow = new AnswerRow();
            answerRow.setQuestion(question);
            answerRow.setComments(answerTo.getComments());
            answerRow.setPiGrade(answerTo.getPiGrade());
            sacsRows.put(answerRow.getQuestion(), answerRow);
            return answerRow;
        }).collect(Collectors.toList());

        formDao.saveForm(answers);
    }

    public void saveAssessmentAnswer(AssessmentAnswersTo assessmentAnswersTo) {
        final List<AssessmentRow> answers = assessmentAnswersTo.getAnswerList().stream().map(assessmentAnswerTo -> {
            final AssessmentRow assessmentRow = new AssessmentRow();
            assessmentRow.setQuestion(getQuestion(assessmentAnswerTo.getNumber()));
            assessmentRow.setElementsPiGrade(assessmentAnswerTo.getPiGrades());
            assessmentRows.put(assessmentRow.getQuestion(), assessmentRow);
            return assessmentRow;
        }).collect(Collectors.toList());
        formDao.saveAssessmentAnswers(answers, sacsRows);
    }

    public void savePlannerAnswer(PlannerAnswersTo plannerAnswersTo) {
        final List<PlannerRow> answers = plannerAnswersTo.getAnswerList().stream().map(plannerAnswerTo -> {
            final PlannerRow plannerRow = new PlannerRow();
            plannerRow.setQuestion(getQuestion(plannerAnswerTo.getNumber()));
            plannerRow.setElement(plannerAnswerTo.getElement());
            plannerRow.setOwnership(plannerAnswerTo.getOwnership());
            plannerRow.setTask(plannerAnswerTo.getTask());
            plannerRow.setPlanned(plannerAnswerTo.getPlanned());
            plannerRows.putIfAbsent(plannerRow.getQuestion(), new HashMap<>()).put(plannerRow.getElement(), plannerRow);
            return plannerRow;
        }).collect(Collectors.toList());
        formDao.savePlannerAnswers(answers, sacsRows, assessmentRows);
    }

    public AnswerRow getAnswerRow(Question question) {
        return sacsRows.getOrDefault(question, new AnswerRow());
    }

    public AssessmentRow getAssessmentRow(Question question) {
        return assessmentRows.getOrDefault(question, new AssessmentRow());
    }

    public PlannerRow getPlannerRow(Question question, Element element) {
        plannerRows.putIfAbsent(question, new HashMap<>());
        return plannerRows.get(question).getOrDefault(element, new PlannerRow());
    }

    public List<String> getExistedCompanies() {
        return formDao.getExistedCompanies();
    }
}
