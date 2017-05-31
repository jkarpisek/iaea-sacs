package cz.karpi.iaea.questionnaire.persistence;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

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

@Repository
public class FormDao {

    private static final String EXCEL_TEMPLATE_NAME = "SACS Grid CDP.xlsx";
    private static final String EXCEL_NAME = "SACS Grid CDP-{0}.xlsx";
    private static final String EXCEL_SHEET_SACS_NAME = "1-SACS";
    private static final String EXCEL_SHEET_ASSESSMENT_NAME = "2-Assessment Grid";
    private static final String EXCEL_SHEET_PLANNER_NAME = "-%s";

    private static final Integer FIRST_CELL_INDEX = 0;
    private static final Integer SUBCATEGORY_CELL_INDEX = 1;
    private static final Integer QUESTION_CELL_INDEX = 2;
    private static final Integer ADDITIONAL_COMMENTS_CELL_INDEX = 2;
    private static final Integer PI_GRADE_CELL_INDEX = 3;
    private static final Integer COMMENTS_CELL_INDEX = 4;

    private static final Logger LOGGER = LoggerFactory.getLogger(FormDao.class);

    private File excelFile;
    private Map<Element, Integer> assessmentElementColumn;
    private Map<Element, Integer> plannerElementIndex;
    private Map<Quarter, Integer> plannerQuarterIndex;

    public void reset() {
        excelFile = null;
        assessmentElementColumn = new HashMap<>();
        plannerElementIndex = new HashMap<>();
        plannerQuarterIndex = new HashMap<>();
    }

    private Workbook getWorkbook() {
        try {
            final FileInputStream fis = new FileInputStream(excelFile);
            final Workbook workbook = new XSSFWorkbook(fis);
            fis.close();
            return workbook;
        } catch (Exception e) {
            throw new DaoExpcetion("Load file failed", e);
        }
    }

    private void saveWorkbook(Workbook workbook) {
        try {
            final FileOutputStream os = new FileOutputStream(excelFile);
            workbook.write(os);
            os.close();
        } catch (Exception e) {
            throw new DaoExpcetion("Save file failed", e);
        }
    }

    public void copyForm(String companyName) {
        try {
            final File source = new File(EXCEL_TEMPLATE_NAME);
            excelFile = new File(MessageFormat.format(EXCEL_NAME, companyName));
            FileCopyUtils.copy(source, excelFile);
        } catch (IOException e) {
            throw new DaoExpcetion("Copy template file failed", e);
        }
    }

    @Async
    public void saveForm(List<AnswerRow> answerRows) {
        if (!answerRows.isEmpty()) {
            save(EXCEL_SHEET_SACS_NAME, (mySheet) -> {
                answerRows.forEach(answerRow -> {
                    final Row row = mySheet.getRow(getRowNum(mySheet, FIRST_CELL_INDEX, answerRow.getQuestion().getNumber()));
                    if (answerRow.getQuestion().getType().equals(EQuestionType.ADDITIONAL_COMMENT)) {
                        row.getCell(ADDITIONAL_COMMENTS_CELL_INDEX).setCellValue(answerRow.getComments());
                    } else if (answerRow.getQuestion().getType().equals(EQuestionType.WITH_PIGRADE)) {
                        row.getCell(PI_GRADE_CELL_INDEX).setCellValue(getOrEmpty(answerRow.getPiGrade()));
                        row.getCell(COMMENTS_CELL_INDEX).setCellValue(answerRow.getComments());
                    }
                });
            });
        }
    }

    private Integer getRowNum(Sheet mySheet, Integer column, String content) {
        return IntStream.range(0, mySheet.getLastRowNum())
            .filter(row -> mySheet.getRow(row).getCell(column) != null && content.equals(mySheet.getRow(row).getCell(column).getStringCellValue()))
            .findFirst().orElseThrow(() -> new DaoExpcetion("Row #" + content + " not found", null));
    }

    public List<Category> getSACSDefinition() {
        final Sheet mySheet = getWorkbook().getSheet(EXCEL_SHEET_SACS_NAME);
        final List<Category> categories = new ArrayList<>();
        Category category = null;
        SubCategory subCategory = null;
        for (Row row : mySheet) {
            if (row.getRowNum() > 0) {
                final String firstCellContent = row.getCell(FIRST_CELL_INDEX).getStringCellValue();
                if (firstCellContent.matches("[A-Z]+-[0-9]+")) {
                    final String subCategoryName = row.getCell(SUBCATEGORY_CELL_INDEX).getStringCellValue();
                    if (!subCategoryName.isEmpty()) {
                        subCategory = new SubCategory();
                        subCategory.setName(subCategoryName);
                        subCategory.setCategory(category);
                        category.getSubCategories().add(subCategory);
                    }
                    final Question question = new Question();
                    question.setNumber(firstCellContent);
                    question.setQuestion(row.getCell(QUESTION_CELL_INDEX).getStringCellValue());
                    question.setSubCategory(subCategory);
                    question.setType(EQuestionType.WITH_PIGRADE);
                    subCategory.getQuestions().add(question);
                } else if (firstCellContent.matches("[A-Z]+-X")
                           || row.getCell(SUBCATEGORY_CELL_INDEX).getStringCellValue().equalsIgnoreCase("Additional written comments:")) {
                    final Question question = new Question();
                    question.setNumber(firstCellContent);
                    question.setQuestion(row.getCell(QUESTION_CELL_INDEX).getStringCellValue());
                    question.setSubCategory(subCategory);
                    question.setType(EQuestionType.ADDITIONAL_COMMENT);
                    subCategory.getQuestions().add(question);
                } else if (firstCellContent.matches("[0-9]+\\).+")) {
                    category = new Category();
                    category.setName(firstCellContent);
                    categories.add(category);
                }
            }
        }
        return categories;
    }

    public List<Category> getSACSData(String companyName) {
        final Sheet mySheet = getWorkbook().getSheet(EXCEL_SHEET_SACS_NAME);
        final List<Category> categories = new ArrayList<>();
        Category category = null;
        SubCategory subCategory = null;
        for (Row row : mySheet) {
            if (row.getRowNum() > 0) {
                final String firstCellContent = row.getCell(FIRST_CELL_INDEX).getStringCellValue();
                if (firstCellContent.matches("[A-Z]+-[0-9]+")) {
                    final String subCategoryName = row.getCell(SUBCATEGORY_CELL_INDEX).getStringCellValue();
                    if (!subCategoryName.isEmpty()) {
                        subCategory = new SubCategory();
                        subCategory.setName(subCategoryName);
                        subCategory.setCategory(category);
                        category.getSubCategories().add(subCategory);
                    }
                    final Question question = new Question();
                    question.setNumber(firstCellContent);
                    question.setQuestion(row.getCell(QUESTION_CELL_INDEX).getStringCellValue());
                    question.setSubCategory(subCategory);
                    question.setType(EQuestionType.WITH_PIGRADE);
                    final AnswerRow answerRow = new AnswerRow();
                    answerRow.setQuestion(question);
                    answerRow.setPiGrade(Double.valueOf(row.getCell(PI_GRADE_CELL_INDEX).getNumericCellValue()).intValue());
                    answerRow.setComments(row.getCell(COMMENTS_CELL_INDEX).getStringCellValue());
                } else if (firstCellContent.matches("[A-Z]+-X")
                           || row.getCell(SUBCATEGORY_CELL_INDEX).getStringCellValue().equalsIgnoreCase("Additional written comments:")) {
                    final Question question = new Question();
                    question.setNumber(firstCellContent);
                    question.setQuestion(row.getCell(QUESTION_CELL_INDEX).getStringCellValue());
                    question.setSubCategory(subCategory);
                    question.setType(EQuestionType.ADDITIONAL_COMMENT);
                    final AnswerRow answerRow = new AnswerRow();
                    answerRow.setComments(row.getCell(ADDITIONAL_COMMENTS_CELL_INDEX).getStringCellValue());
                    answerRow.setQuestion(question);
                } else if (firstCellContent.matches("[0-9]+\\).+")) {
                    category = new Category();
                    category.setName(firstCellContent);
                    categories.add(category);
                }
            }
        }
        return categories;
    }

    public List<Element> getAssessmentDefinition() {
        final Sheet mySheet = getWorkbook().getSheet(EXCEL_SHEET_ASSESSMENT_NAME);
        final Row row = mySheet.getRow(1);
        final Iterable<Cell> cellIterable = row::cellIterator;
        return StreamSupport.stream(cellIterable.spliterator(), false).filter(cell -> cell.getColumnIndex() >= 4 && !cell.getStringCellValue().equals("")).map(cell -> {
            final Element element = new Element(cell.getStringCellValue());
            assessmentElementColumn.put(element, cell.getColumnIndex());
            plannerElementIndex.put(element, cell.getColumnIndex() - 4);
            return element;
        }).collect(Collectors.toList());
    }

    @Async
    public void saveAssessmentAnswers(List<AssessmentRow> answerRows) {
        if (!answerRows.isEmpty()) {
            save(EXCEL_SHEET_ASSESSMENT_NAME, (mySheet) ->
                answerRows.forEach(answerRow -> {
                    final Row row = mySheet.getRow(getRowNum(mySheet, FIRST_CELL_INDEX, answerRow.getQuestion().getNumber()));
                    /*TODO ulozit AnswerRow*/
                    answerRow.getElementsPiGrade()
                        .forEach((element, value) -> row.getCell(assessmentElementColumn.get(element)).setCellValue(getOrEmpty(value)));
                })
            );
        }
    }

    @Async
    public void savePlannerAnswers(List<PlannerRow> answerRows) {
        if (!answerRows.isEmpty()) {
            save(convertNumberToSheetName(answerRows.get(0).getQuestion().getNumber()), (mySheet) ->
                answerRows.forEach(answerRow -> {
                    final Row
                        row =
                        mySheet
                            .getRow(getRowNum(mySheet, FIRST_CELL_INDEX, answerRow.getQuestion().getNumber()) + getPlannerElementIndex(answerRow.getElement()));
                /*TODO ulozit AssessmentRow*/
                    row.getCell(5).setCellValue(answerRow.getTask());
                    row.getCell(6).setCellValue(answerRow.getOwnership());
                    answerRow.getPlanned().forEach((quarter, value) -> row.getCell(getQuarterIndex(quarter)).setCellValue(value ? "x" : ""));
                })
            );
        }
    }

    private synchronized void save(String sheetName, Consumer<Sheet> fillData) {
        LOGGER.info("Save Excel - start");
        final Workbook workbook = getWorkbook();
        fillData.accept(getSheet(workbook, sheetName));
        saveWorkbook(workbook);
        LOGGER.info("Save Excel - end");
    }

    private Integer getPlannerElementIndex(Element element) {
        return plannerElementIndex.get(element);
    }

    private Integer getQuarterIndex(Quarter quarter) {
        return plannerQuarterIndex.get(quarter);
    }

    private String convertNumberToSheetName(String number) {
        return String.format(EXCEL_SHEET_PLANNER_NAME, number.split("-")[0]);
    }

    private Sheet getSheet(Workbook workbook, String sheetName) {
        return IntStream.range(0, workbook.getNumberOfSheets()).filter(value -> workbook.getSheetName(value).endsWith(sheetName))
            .mapToObj(workbook::getSheetAt).findFirst().orElseThrow(() -> new RuntimeException("Sheet with name " + sheetName + " not found"));
    }

    public List<Year> getPlannerDefinition(String number, List<Year> defaultYears) {
        final Sheet sheet = getSheet(getWorkbook(), convertNumberToSheetName(number));
        final Row row = sheet.getRow(1);
        final Iterable<Cell> cellIterable = row::cellIterator;
        //todo najit stavajici, kdyz nejsou vzit, pak default zapsat do XLS a vratit
        final Map<Integer, Year> years = new HashMap<>();
        final List<Quarter> quarters = StreamSupport.stream(cellIterable.spliterator(), false)
            .filter(cell -> cell.getStringCellValue().replaceAll("\\s", "").matches("[1-4]Q20[0-9]{2}"))
            .map(cell -> {
                final String[] split = cell.getStringCellValue().replaceAll("\\s", "").split("Q");
                final Integer yearValue = Integer.parseInt(split[1]);
                years.putIfAbsent(yearValue, new Year(yearValue));
                final Year year = years.get(yearValue);
                final Integer q = Integer.parseInt(split[0]);
                final Quarter quarter = new Quarter(q, year);
                year.getQuarters().add(quarter);
                plannerQuarterIndex.put(quarter, cell.getColumnIndex());
                return quarter;
            })
            .collect(Collectors.toList());
        if (!quarters.isEmpty()) {
            final List<Year> yearsSorted = new ArrayList<>(years.values());
            yearsSorted.sort(Comparator.comparing(Year::getName));
            return yearsSorted;
        } else {
            throw new RuntimeException("Load quarters is not supported");
            //todo return defaultYears;
        }
    }

    private String getOrEmpty(Object value) {
        return value != null ? value.toString() : "";
    }
}
