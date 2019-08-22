package net.rcarz.kantime;

import java.io.File;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Assert;

public class ReadExcel {

  Object[][] tabArray = null;
  Workbook workbook = null;

  /**
   * read data from excel from given file and sheet.
   * 
   * @param sheetName sheetName
   * @return object of data
   * @throws Exception Exception
   */
  public Object[][] getEntireExcelSheetData(String excelNameWithFullPath,
      String sheetName) throws Exception {
    try {

      final String excleFilePath = excelNameWithFullPath;
      // utils.getConfigProperties("TestCaseFolder") + "//" + excelName;

      System.out.println("Excel Opened");
      // Creating a Workbook from an Excel file (.xls or .xlsx)
      workbook = WorkbookFactory.create(new File(excleFilePath));

      // Getting the Sheet by sheet name
      Sheet sheet = workbook.getSheet(sheetName);

      // get the total number of rows and columns in the sheet
      int totalCols = sheet.getRow(0).getLastCellNum();
      int totalRows = sheet.getLastRowNum();

      tabArray = new String[totalRows][totalCols];

      int startRow = 1;
      int startCol = 0;
      int ci = 0;
      int cj = 0;

      FormulaEvaluator evaluator = workbook.getCreationHelper()
          .createFormulaEvaluator();
      // loop through the excel and get data
      for (int i = startRow; i <= totalRows; i++, ci++) {
        cj = 0;
        for (int j = startCol; j < totalCols; j++, cj++) {
          Row row = sheet.getRow(i);
          Cell cell = row.getCell(j);

          if (cell == null) {
            tabArray[ci][cj] = "";
          } else {
            CellValue cellValue = evaluator.evaluate(cell);
            switch (cellValue.getCellTypeEnum()) {
              case BOOLEAN:
                tabArray[ci][cj] = cell.getBooleanCellValue();
                break;
              case STRING:
                tabArray[ci][cj] = cell.getRichStringCellValue().getString();
                break;
              case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                  SimpleDateFormat dateFormat = new SimpleDateFormat(
                      "MM/dd/yyyy");
                  tabArray[ci][cj] = dateFormat.format(cell.getDateCellValue())
                      .toString();
                } else {
                  cell.setCellType(CellType.STRING);
                  tabArray[ci][cj] = cell.getRichStringCellValue().getString();
                }
                break;
              case FORMULA:
                tabArray[ci][cj] = cell.getCellFormula();
                break;
              case BLANK:
                tabArray[ci][cj] = "";
                break;
              default:
                tabArray[ci][cj] = "";
            }
          }

        }
      }
    } catch (Exception e) {
      // Closing the workbook
      workbook.close();
      Assert.assertTrue(false,
          "Exception happend while reading excel : " + e.toString());
      System.out
          .println("Exception happend while reading excel : " + e.toString());
    } finally {
      workbook.close();
    }
    // return object
    return tabArray;
  }

  /**
   * read data from excel from given file and sheet.
   * 
   * @param excelNameWithFullPath excelNameWithFullPath
   * @param sheetName             sheetName
   * @return String Array with data
   * @throws Exception Exception
   */
  public String[][] getEntireExcelSheetDataAsStringArray(
      String excelNameWithFullPath, String sheetName) throws Exception {
    String[][] rowArray = null;
    try {
      final String excleFilePath = excelNameWithFullPath;
      // utils.getConfigProperties("TestCaseFolder") + "//" + excelName;

      System.out.println("Excel Opened to get the Test case master file");
      // Creating a Workbook from an Excel file (.xls or .xlsx)
      workbook = WorkbookFactory.create(new File(excleFilePath));

      // Getting the Sheet by sheet name
      Sheet sheet = workbook.getSheet(sheetName);

      // get the total number of rows and columns in the sheet
      int totalCols = sheet.getRow(0).getLastCellNum();
      int totalRows = sheet.getLastRowNum();

      rowArray = new String[totalRows][totalCols];

      int startRow = 1;
      int startCol = 0;
      int ci = 0;
      int cj = 0;

      // loop through the excel and get data
      for (int i = startRow; i <= totalRows; i++, ci++) {
        cj = 0;
        for (int j = startCol; j < totalCols; j++, cj++) {
          Row row = sheet.getRow(i);
          Cell cell = row.getCell(j);

          if (cell == null) {
            tabArray[ci][cj] = "";
          } else {
            switch (cell.getCellTypeEnum()) {

              // CANNOT READ BOOLEAN
              // case BOOLEAN:
              // RowArray[ci][cj] = cell.getBooleanCellValue();
              // break;

              case STRING:
                rowArray[ci][cj] = cell.getRichStringCellValue().getString();
                break;
              case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                  SimpleDateFormat dateFormat = new SimpleDateFormat(
                      "MM/dd/yyyy");
                  rowArray[ci][cj] = dateFormat.format(cell.getDateCellValue())
                      .toString();
                } else {
                  cell.setCellType(CellType.STRING);
                  rowArray[ci][cj] = cell.getRichStringCellValue().getString();
                }
                break;
              case FORMULA:
                rowArray[ci][cj] = cell.getCellFormula();
                break;
              case BLANK:
                rowArray[ci][cj] = "";
                break;
              default:
                rowArray[ci][cj] = "";
            }
          }

        }
      }
    } catch (Exception e) {
      // Closing the workbook
      Assert.assertTrue(false,
          "Exception happend while reading excel : " + e.toString());
      System.out
          .println("Exception happend while reading excel : " + e.toString());
      workbook.close();

    } finally {
      workbook.close();
    }
    // return object
    return rowArray;
  }

}