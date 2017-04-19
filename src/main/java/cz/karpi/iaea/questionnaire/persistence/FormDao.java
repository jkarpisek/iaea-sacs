package cz.karpi.iaea.questionnaire.persistence;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import cz.karpi.iaea.questionnaire.model.AbstractAnswerRow;
import cz.karpi.iaea.questionnaire.model.AdditionalCommentsRow;
import cz.karpi.iaea.questionnaire.model.AnswerWithPiGradeRow;
import cz.karpi.iaea.questionnaire.model.Category;
import cz.karpi.iaea.questionnaire.model.SACSForm;
import cz.karpi.iaea.questionnaire.model.SubCategory;

@Repository
public class FormDao {

    private static final String EXCEL_TEMPLATE_NAME = "SACS Grid CDP.xlsx";
    private static final String EXCEL_NAME = "SACS Grid CDP-{0}.xlsx";
    private static final String EXCEL_SHEET_NAME = "1-SACS";

    private static final Integer FIRST_CELL_INDEX = 0;
    private static final Integer SUBCATEGORY_CELL_INDEX = 1;
    private static final Integer QUESTION_CELL_INDEX = 2;
    private static final Integer ADDITIONAL_COMMENTS_CELL_INDEX = 2;
    private static final Integer PI_GRADE_CELL_INDEX = 3;
    private static final Integer COMMENTS_CELL_INDEX = 4;

    private XSSFWorkbook loadWorkbook(String fileName) {
        try {
            final File myFile = new File(fileName);
            final FileInputStream fis = new FileInputStream(myFile);
            return new XSSFWorkbook(fis);
        } catch (Exception e) {
            throw new DaoExpcetion("Load file failed", e);
        }
    }

    private void saveWorkbook(Workbook workbook, String fileName) {
        try {
            FileOutputStream os = new FileOutputStream(new File(fileName));
            workbook.write(os);
        } catch (Exception e) {
            throw new DaoExpcetion("Save file failed", e);
        }
    }

    public void copyForm(String companyName) {
        final File source = new File(EXCEL_TEMPLATE_NAME);
        final File dest = new File(MessageFormat.format(EXCEL_NAME, companyName));
        try {
            FileCopyUtils.copy(source, dest);
        } catch (IOException e) {
            throw new DaoExpcetion("Copy template file failed", e);
        }
    }

    public void saveForm(String companyName, List<AbstractAnswerRow> answerRows) {
        final String excelName = MessageFormat.format(EXCEL_NAME, companyName);
        final Workbook workbook = loadWorkbook(excelName);
        final Sheet mySheet = workbook.getSheet(EXCEL_SHEET_NAME);

        answerRows.forEach(answerRow -> {
            final Row row = mySheet.getRow(answerRow.getRowNum());
            if (answerRow.getClass().isAssignableFrom(AdditionalCommentsRow.class)) {
                row.getCell(ADDITIONAL_COMMENTS_CELL_INDEX).setCellValue(answerRow.getComments());
            } else if (answerRow.getClass().isAssignableFrom(AnswerWithPiGradeRow.class)) {
                row.getCell(PI_GRADE_CELL_INDEX).setCellValue(((AnswerWithPiGradeRow) answerRow).getPiGrade());
                row.getCell(COMMENTS_CELL_INDEX).setCellValue(answerRow.getComments());
            }
        });
        saveWorkbook(workbook, excelName);
    }

    public SACSForm getSACSForm(String companyName) {
        final String excelName = MessageFormat.format(EXCEL_NAME, companyName);
        final Sheet mySheet = loadWorkbook(excelName).getSheet(EXCEL_SHEET_NAME);
        final SACSForm sacsForm = new SACSForm();
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
                    final AnswerWithPiGradeRow answerWithPiGradeRow = new AnswerWithPiGradeRow();
                    answerWithPiGradeRow.setRowNum(row.getRowNum());
                    answerWithPiGradeRow.setNumber(firstCellContent);
                    answerWithPiGradeRow.setQuestion(row.getCell(QUESTION_CELL_INDEX).getStringCellValue());
                    answerWithPiGradeRow.setPiGrade(Double.valueOf(row.getCell(PI_GRADE_CELL_INDEX).getNumericCellValue()).intValue());
                    answerWithPiGradeRow.setComments(row.getCell(COMMENTS_CELL_INDEX).getStringCellValue());
                    answerWithPiGradeRow.setSubCategory(subCategory);
                    subCategory.getAnswerRows().add(answerWithPiGradeRow);
                } else if (firstCellContent.matches("[A-Z]+-X")
                           || row.getCell(SUBCATEGORY_CELL_INDEX).getStringCellValue().equalsIgnoreCase("Additional written comments:")) {
                    final AdditionalCommentsRow additionalCommentsRow = new AdditionalCommentsRow();
                    additionalCommentsRow.setRowNum(row.getRowNum());
                    additionalCommentsRow.setNumber(firstCellContent);
                    additionalCommentsRow.setQuestion(row.getCell(QUESTION_CELL_INDEX).getStringCellValue());
                    additionalCommentsRow.setComments(row.getCell(ADDITIONAL_COMMENTS_CELL_INDEX).getStringCellValue());
                    additionalCommentsRow.setSubCategory(subCategory);
                    subCategory.getAnswerRows().add(additionalCommentsRow);
                } else if (firstCellContent.matches("[0-9]+\\).+")) {
                    category = new Category();
                    category.setName(firstCellContent);
                    sacsForm.getCategories().add(category);
                }
            }
        }
        return sacsForm;
    }
}
