package com.spring.boot.exam_service.utils;

import org.apache.poi.ss.usermodel.*;

public class ExcelUtil {
    public static String getCellValueFromCell(Cell cell) {
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
        } finally {
            return cellValue;
        }
    }


    public static void setColorGreenOrRed(Cell cell, boolean check) {
        Workbook workbook= cell.getSheet().getWorkbook();
        CellStyle passedStyle = workbook.createCellStyle();
        CellStyle failedStyle = workbook.createCellStyle();
        Font passedFont = workbook.createFont();
        passedFont.setColor(IndexedColors.GREEN.getIndex());
        passedStyle.setFont(passedFont);
        Font failedFont = workbook.createFont();
        failedFont.setColor(IndexedColors.RED.getIndex());
        failedStyle.setFont(failedFont);
        if (check) {
            cell.setCellStyle(passedStyle);
        } else {
            cell.setCellStyle(failedStyle);
        }

    }

}
