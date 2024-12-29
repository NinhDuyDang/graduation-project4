package com.book_store.controller;

import com.book_store.entity.Order;
import com.book_store.service.OrderPDFService;
import com.book_store.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;

@RestController
public class OrderPDFController {
    @Autowired
    private OrderPDFService orderPDFService;

    @Autowired
    private OrderService orderService; // Giả sử bạn có service để lấy dữ liệu đơn hàng

    @GetMapping("/order/{id}/export-pdf")
    public ResponseEntity<byte[]> exportOrderToPDF(@PathVariable("id") int id) {
        // Lấy thông tin đơn hàng từ database
        Order order = orderService.getById(id);

        // Sinh file PDF
        ByteArrayOutputStream pdfOutput = orderPDFService.generateOrderPDF(order);

        // Trả file PDF về phía client
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=order_" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfOutput.toByteArray());
    }
}
