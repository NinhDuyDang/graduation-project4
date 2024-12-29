package com.book_store.service;

import com.book_store.entity.Order;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class OrderExcelService {
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_CUSTOMER_ID = 1;
    private static final int COLUMN_RECEIVER_NAME = 2;
    private static final int COLUMN_RECEIVER_PHONE = 3;
    private static final int COLUMN_RECEIVER_ADDRESS = 4;
    private static final int COLUMN_PROMOTION_ID = 5;
    private static final int COLUMN_SHIPPING_UNIT_ID = 6;
    private static final int COLUMN_PRICE = 7;
    private static final int COLUMN_PAYMENT_METHOD = 8;
    private static final int COLUMN_STATUS = 9;
    private static final int COLUMN_PAYMENT_STATUS = 10;
    private static final int COLUMN_CREATE_AT = 11;
    private static final int COLUMN_UPDATED_AT = 12;


    public void createList(Order order, Row row) {
        Cell cell = row.createCell(0);
        cell.setCellValue(order.getId());
        cell = row.createCell(1);
        cell.setCellValue(order.getCustomer().getName());
        cell = row.createCell(2);
        cell.setCellValue(order.getReceiverName());

        cell = row.createCell(3);
        cell.setCellValue(order.getReceiverPhone());
        cell = row.createCell(4);
        cell.setCellValue(order.getReceiverAddress());

        cell = row.createCell(5);
        if (order.getPromotion() == null || order.getPromotion().getCode() == null) {
            cell.setCellValue("");
        } else {
            cell.setCellValue(order.getPromotion().getCode());
        }

        cell = row.createCell(6);
        cell.setCellValue(order.getShippingUnit().getName());

        cell = row.createCell(7);
        cell.setCellValue(order.getPrice().toString());

        cell = row.createCell(8);
        cell.setCellValue(order.getPaymentMethod());

        cell = row.createCell(9);
        cell.setCellValue(order.getStatus());

        cell = row.createCell(10);
        cell.setCellValue(order.getPaymentStatus());

        cell = row.createCell(11);
        cell.setCellValue(order.getCreatedAt().toString());

        cell = row.createCell(12);
        cell.setCellValue(order.getUpdatedAt().toString());
    }

    public void createHeader(Row row) {
        Cell cell = row.createCell(0);
        cell.setCellValue("Id đơn hàng");
        cell = row.createCell(1);
        cell.setCellValue("Tên đăng nhập khách hàng");
        cell = row.createCell(2);
        cell.setCellValue("Tên người nhận");
        cell = row.createCell(3);
        cell.setCellValue("Số điện thoại người nhận");
        cell = row.createCell(4);
        cell.setCellValue("Địa chỉ người nhận");
        cell = row.createCell(5);
        cell.setCellValue("Mã giảm giá");
        cell = row.createCell(6);
        cell.setCellValue("Đơn vị vận chuyển");
        cell = row.createCell(7);
        cell.setCellValue("Giá");
        cell = row.createCell(8);
        cell.setCellValue("Phương thức thanh toán");
        cell = row.createCell(9);
        cell.setCellValue("Trạng thái thanh toán");
        cell = row.createCell(10);
        cell.setCellValue("Trạng thái");
        cell = row.createCell(11);
        cell.setCellValue("Ngày tạo");
        cell = row.createCell(12);
        cell.setCellValue("Ngày update");
    }


    // Get Workbook
    public static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
        Workbook workbook = null;
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }
        return workbook;
    }
    // Get cell value
    public Object getCellValue(Cell cell) {
        int cellType = cell.getCellType();
        Object cellValue = null;
        switch (cellType) {
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case Cell.CELL_TYPE_FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                cellValue = cell.getNumericCellValue();
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BLANK:
            case Cell.CELL_TYPE_ERROR:
                break;
            default:
                break;
        }
        return cellValue;
    }
}
