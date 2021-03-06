package cz.karpi.iaea.questionnaire.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
import cz.karpi.iaea.questionnaire.model.MenuEntry;
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
@DependsOn("asyncExecutor")
public class FormService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormService.class);

    private String companyName;

    private String intro;

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

    @Async
    public void loadIntro() {
        this.intro = formDao.getIntro();
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

    public List<MenuEntry> getQuestionnaireMenu() {
        if (this.categories == null) return null;

        List<MenuEntry> result = new ArrayList<>();

        for (Category category: categories) {
            for (SubCategory subCategory : category.getSubCategories()) {
                MenuEntry entry = new MenuEntry();
                entry.setCategory(category.getName());
                entry.setSubCategory(subCategory.getName());
                entry.setActive(true);

                result.add(entry);
            }
        }
        return result;
    }

    public Map<Question, AnswerRow> getSacsRows() {
        return sacsRows;
    }

    public List<MenuEntry> getAssessmentMenu () {
        if (this.categories == null) return null;
        //go throu all categories and find out whether some data is filled in.
        //if so, add it into menu as active link otherwise as inactive
        List<MenuEntry> result = new ArrayList<>();

        for (Category category: categories) {
            for (SubCategory subCategory: category.getSubCategories()) {
                MenuEntry entry = new MenuEntry();
                entry.setCategory(category.getName());
                entry.setSubCategory(subCategory.getName());
                //how to find out whether something is filled in??
                //subCategory by měla být vidět, když jakákoliv otázek pod ní je vyplněná, tzn.
                //for all subCategory.questions najdi odpověď a čekni, jestli je tam piGrade?
                //a udělat to přes lambda
                //this.sacsRows.get(subCategory.getName())
                boolean active = subCategory.getQuestions().stream().anyMatch(question -> this.sacsRows.get(question).getPiGrade() != null);
                entry.setActive(active);
                result.add(entry);
            }
        }
        return result;
    }

    private boolean getPlannerMenuStatus(SubCategory subCategory) {
        for (Question question: subCategory.getQuestions()) {
            for (Map.Entry<Element, Integer> piGrade : this.assessmentRows.get(question).getElementsPiGrade().entrySet()) {
                if (piGrade.getValue() != null) return true;
            }
        }
        return false;
    }

    public List<MenuEntry> getPlannerMenu () {
        if (this.categories == null) return null;
        //go throu all categories and find out whether some data is filled in.
        //if so, add it into menu as active link otherwise as inactive
        List<MenuEntry> result = new ArrayList<>();

        for (Category category: categories) {
            for (SubCategory subCategory: category.getSubCategories()) {
                MenuEntry entry = new MenuEntry();
                entry.setCategory(category.getName());
                entry.setSubCategory(subCategory.getName());
                //how to find out whether something is filled in??
                //subCategory by měla být vidět, když jakákoliv otázek pod ní je vyplněná, tzn.
                //for all subCategory.questions najdi odpověď a čekni, jestli je tam piGrade?
                //a udělat to přes lambda
                //this.sacsRows.get(subCategory.getName())

                entry.setActive(getPlannerMenuStatus(subCategory));

                result.add(entry);
            }
        }
        return result;
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

    public String getIntro() {
        while (intro == null) {
            try {
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                throw new RuntimeException("Wait for load intro failed", e);
            }
        }
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
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
        final Map<Question, Map<Element, PlannerRow>> currentPlannerRows = new HashMap<>();
        final List<PlannerRow> answers = plannerAnswersTo.getAnswerList().stream().map(plannerAnswerTo -> {
            final PlannerRow plannerRow = new PlannerRow();
            plannerRow.setQuestion(getQuestion(plannerAnswerTo.getNumber()));
            plannerRow.setElement(plannerAnswerTo.getElement());
            plannerRow.setOwnership(plannerAnswerTo.getOwnership());
            plannerRow.setTask(plannerAnswerTo.getTask());
            plannerRow.setPlanned(plannerAnswerTo.getPlanned());
            plannerRows.putIfAbsent(plannerRow.getQuestion(), new HashMap<>()).put(plannerRow.getElement(), plannerRow);
            currentPlannerRows.put(plannerRow.getQuestion(), plannerRows.get(plannerRow.getQuestion()));
            return plannerRow;
        }).collect(Collectors.toList());
        formDao.savePlannerAnswers(answers, sacsRows, assessmentRows);
        formDao.saveCdp(currentPlannerRows);
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
