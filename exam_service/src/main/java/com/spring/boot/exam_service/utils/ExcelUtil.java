package com.spring.boot.exam_service.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

public class ExcelUtil {
    public static String getCellValueFromCell (Cell cell) {
        String cellValue = "";
        try {
            if (cell != null) {
                switch (cell.getCellType()) {
                    case STRING:
                        cellValue = cell.getStringCellValue();
                        break;
                    case NUMERIC:
                        cell.setCellType(CellType.STRING);
                        cellValue = cell.getStringCellValue();
                        break;
                    case BOOLEAN:
                        cellValue = String.valueOf(cell.getBooleanCellValue());
                        break;
                    case FORMULA:
                        cellValue = cell.getCellFormula();
                        break;
                    default:
                        cellValue = ""; // Hoặc xử lý trường hợp khác tùy ý
                        break;
                }

            }


        } catch (Exception e) {
            // Xử lý ngoại lệ
            e.printStackTrace();
        }
        finally {
            return cellValue;
        }
    }
}
