package com.book_store.controller.customer_controller;

import com.book_store.entity.Order;
import com.book_store.entity.Product;
import com.book_store.service.ExcelService;
import com.book_store.service.OrderExcelService;
import com.book_store.service.OrderService;
import com.book_store.service.ProductService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class ExportController {
    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private OrderExcelService orderExcelService;

    @GetMapping("/exportproducts")
    public ResponseEntity<InputStreamResource> exportProductsToExcel() {
        List<Product> allProducts = productService.getAllProducts();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss");
        String fileName = "productList" + sdf.format(date) + ".xlsx";

        try {
            // Tạo workbook Excel
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Product List");

            // Tạo header cho file Excel
            Row row0 = sheet.createRow(0);
            excelService.createHeader(row0);

            // Thêm dữ liệu vào file Excel
            int rownum = 1;
            for (Product product : allProducts) {
                Row row = sheet.createRow(rownum++);
                excelService.createList(product, row);
            }

            // Ghi dữ liệu vào ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            //workbook.close();

            // Trả file về phía client
            ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exportorder")
    public ResponseEntity<InputStreamResource> exportOrdersToExcel(
            @RequestParam(name = "pageNumber") int currentPage,
            @RequestParam(name = "month") int month,
            @RequestParam(name = "year") int year) {
        Page<Order> page = orderService.listOrderByMonthAndYear(currentPage, month, year);
        List<Order> orders = page.getContent();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy--HH-mm-ss");
        String fileName = "orderList" + sdf.format(date) + ".xlsx";

        try {
            // Tạo workbook Excel
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Danh sách đơn hàng");

            // Tạo header cho file Excel
            Row row0 = sheet.createRow(0);
            orderExcelService.createHeader(row0);

            // Thêm dữ liệu vào file Excel
            int rownum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rownum++);
                orderExcelService.createList(order, row);
            }

            // Ghi dữ liệu vào ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            //workbook.close();

            // Trả file về phía client
            ByteArrayInputStream inputStream = new ByteArrayInputStream(out.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
