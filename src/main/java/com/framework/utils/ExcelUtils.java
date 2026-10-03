package com.framework.utils;

import com.framework.config.ConfigReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads test data from an .xlsx workbook.
 * <p>
 * Conventions: row 1 is the header row; every following row is one test-data set.
 * Blank rows are skipped. If a sheet has a "Run" column, rows marked N are skipped.
 * All cell values are returned as trimmed Strings (via DataFormatter) so numbers, dates
 * and text behave consistently.
 */
public final class ExcelUtils {

    private ExcelUtils() {
    }

    /** Reads a sheet from the workbook configured in testdata.path. */
    public static Object[][] getData(String sheetName) {
        return getData(ConfigReader.get("testdata.path"), sheetName);
    }

    /** Returns one {@code Map<String,String>} (header -> value) per row, shaped for a TestNG DataProvider. */
    public static Object[][] getData(String filePath, String sheetName) {
        List<Map<String, String>> rows = readSheet(filePath, sheetName);
        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i);
        }
        return data;
    }

    public static List<Map<String, String>> readSheet(String filePath, String sheetName) {
        List<Map<String, String>> result = new ArrayList<>();
        try (FileInputStream in = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + filePath);
            }
            DataFormatter formatter = new DataFormatter();
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return result;
            }
            int columns = headerRow.getLastCellNum();
            List<String> headers = new ArrayList<>();
            for (int c = 0; c < columns; c++) {
                Cell cell = headerRow.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                headers.add(cell == null ? "" : formatter.formatCellValue(cell).trim());
            }

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                Map<String, String> record = new LinkedHashMap<>();
                boolean empty = true;
                for (int c = 0; c < columns; c++) {
                    if (headers.get(c).isEmpty()) {
                        continue;
                    }
                    Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    String value = cell == null ? "" : formatter.formatCellValue(cell).trim();
                    if (!value.isEmpty()) {
                        empty = false;
                    }
                    record.put(headers.get(c), value);
                }
                if (empty) {
                    continue;
                }
                if ("N".equalsIgnoreCase(record.getOrDefault("Run", "Y"))) {
                    continue;
                }
                result.add(record);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read test data file: " + filePath, e);
        }
        return result;
    }
}
