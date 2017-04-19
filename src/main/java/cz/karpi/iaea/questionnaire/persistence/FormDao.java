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

/**
 * Created by karpi on 12.4.17.
 */
@Repository
public class FormDao {

    private static final String EXCEL_TEMPLATE_NAME = "SACS Grid CDP.xlsx";
    private static final String EXCEL_NAME = "SACS Grid CDP-{0}.xlsx";
    private static final String EXCEL_SHEET_NAME = "1-SACS";

    private XSSFWorkbook loadWorkbook(String fileName) {
        try {
            final File myFile = new File(fileName);
            final FileInputStream fis = new FileInputStream(myFile); // Finds the workbook instance for XLSX file
            return new XSSFWorkbook(fis); // Return first sheet from the XLSX workbook
        } catch (Exception e) {
            throw new DaoExpcetion("", e);
        }
    }

    private void saveWorkbook(Workbook workbook, String fileName) {
        try {
            FileOutputStream os = new FileOutputStream(new File(fileName));
            workbook.write(os);
        } catch (Exception e) {
            throw new DaoExpcetion("", e);
        }
    }

    public void copyForm(String companyName) {
        final File source = new File(EXCEL_TEMPLATE_NAME);
        final File dest = new File(MessageFormat.format(EXCEL_NAME, companyName));
        try {
            FileCopyUtils.copy(source, dest);
        } catch (IOException e) {
            throw new DaoExpcetion("", e);
        }
    }

    public void saveForm(String companyName, List<AbstractAnswerRow> answerRows) {
        final String excelName = MessageFormat.format(EXCEL_NAME, companyName);
        final Workbook workbook = loadWorkbook(excelName);
        final Sheet mySheet = workbook.getSheet(EXCEL_SHEET_NAME);

        answerRows.forEach(answerRow -> {
            final Row row = mySheet.getRow(answerRow.getRowNum());
            row.getCell(4).setCellValue(answerRow.getComments());
        });
        saveWorkbook(workbook, excelName);
    }

    public SACSForm getSACSForm(String companyName) {
        final String excelName = MessageFormat.format(EXCEL_NAME, companyName);
        final Sheet mySheet = loadWorkbook(excelName).getSheet(EXCEL_SHEET_NAME); // Get iterator to all the rows in current sheet
        final SACSForm sacsForm = new SACSForm();
        Category category = null;
        SubCategory subCategory = null;
        for (Row row : mySheet) {
            if (row.getRowNum() > 0) {
                final String firstCellContent = row.getCell(0).getStringCellValue();
                if (firstCellContent.matches("[A-Z]+-[0-9]+")) {
                    //otazka
                    final String subCategoryName = row.getCell(1).getStringCellValue();
                    if (!subCategoryName.isEmpty()) {
                        subCategory = new SubCategory();
                        subCategory.setName(subCategoryName);
                        subCategory.setCategory(category);
                        category.getSubCategories().add(subCategory);
                    }
                    final AnswerWithPiGradeRow answerWithPiGradeRow = new AnswerWithPiGradeRow();
                    answerWithPiGradeRow.setRowNum(row.getRowNum());
                    answerWithPiGradeRow.setNumber(firstCellContent);
                    answerWithPiGradeRow.setQuestion(row.getCell(2).getStringCellValue());
                    answerWithPiGradeRow.setPiGrade(Double.valueOf(row.getCell(3).getNumericCellValue()).intValue());
                    answerWithPiGradeRow.setComments(row.getCell(4).getStringCellValue());
                    answerWithPiGradeRow.setSubCategory(subCategory);
                    subCategory.getAnswerRows().add(answerWithPiGradeRow);
                } else if (firstCellContent.matches("[A-Z]+-X") || row.getCell(1).getStringCellValue().equalsIgnoreCase("Additional written comments:")) {
                    //additional comments
                    final AdditionalCommentsRow additionalCommentsRow = new AdditionalCommentsRow();
                    additionalCommentsRow.setRowNum(row.getRowNum());
                    additionalCommentsRow.setNumber(firstCellContent);
                    additionalCommentsRow.setQuestion(row.getCell(1).getStringCellValue());
                    additionalCommentsRow.setComments(row.getCell(2).getStringCellValue());
                    additionalCommentsRow.setSubCategory(subCategory);
                    subCategory.getAnswerRows().add(additionalCommentsRow);
                } else if (firstCellContent.matches("[0-9]+\\).+")) {
                    //kategorie
                    category = new Category();
                    category.setName(firstCellContent);
                    sacsForm.getCategories().add(category);
                }
            }
        }
        return sacsForm;
    }
}
