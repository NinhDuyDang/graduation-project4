package com.book_store.service;
import com.book_store.entity.Order;
import com.book_store.entity.OrderDetail;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class OrderPDFService {

    public ByteArrayOutputStream generateOrderPDF(Order order) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Tạo PdfWriter và PdfDocument
            PdfWriter writer = new PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);

            // Tạo Document
            Document document = new Document(pdfDoc);
            // Nạp font hỗ trợ Unicode (Thay đổi đường dẫn font theo hệ thống của bạn)
            String fontPath = "C:/Windows/Fonts/arial.ttf"; // Đường dẫn tới font Arial
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);

            // Áp dụng font vào document
            document.setFont(font);

            // Thêm tiêu đề
            document.add(new Paragraph("Hóa đơn ")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Thêm thông tin đơn hàng
            document.add(new Paragraph("Mã  đơn hàng: " + order.getId()));
            document.add(new Paragraph("Ngày tạo: " + order.getCreatedAt().toString()));
            document.add(new Paragraph("Tên khách hàng: " + order.getCustomer().getName()));
            document.add(new Paragraph("Phương thức thanh toán: " + (order.getPaymentMethod() == 0 ? "Ship cod" : "Banking")));
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            document.add(new Paragraph("Tên người nhận: " + order.getReceiverName()));
            document.add(new Paragraph("Số điện thoại: " + order.getReceiverPhone()));
            document.add(new Paragraph("Địa chỉ: " + order.getReceiverAddress()));
            document.add(new Paragraph("Mã giảm giá: " + (order.getPromotion() == null ? "Không có mã giảm giá" : order.getPromotion().getName())));
            document.add(new Paragraph("Đơn vị vận chuyển: " + order.getShippingUnit().getName()));
            document.add(new Paragraph("Trạng thái thanh toán: " + (order.getPaymentStatus() == 0 ? "Chưa thanh toán" : "Đã thanh toán")));
            document.add(new Paragraph("Trạng thái: " +
                    (order.getStatus() == 0 ? "Đang xử lý" :
                            (order.getStatus() == 1 ? "Đang vận chuyển" :
                                    (order.getStatus() == 2 ? "Đã hoàn thành" :
                                            (order.getStatus() == 3 ? "Đã hủy" : "Không xác định"))))));
            document.add(new Paragraph("Tổng giá trị: " + formatter.format(order.getPrice()) + " VND").setMarginBottom(20));

            // Tạo bảng sản phẩm
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Thêm tiêu đề cột
            table.addHeaderCell("Tên sản phẩm");
            table.addHeaderCell("Số lượng");
            table.addHeaderCell("Giá (VND)");
            table.addHeaderCell("Thành tiền (VND)");

            // Thêm dữ liệu sản phẩm vào bảng
            for (OrderDetail item : order.getOrderDetails()) {
                table.addCell(item.getProduct().getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(formatter.format(item.getPrice())));
                table.addCell(String.valueOf(formatter.format(
                        new BigDecimal(item.getQuantity()).multiply(item.getPrice()))));
            }

            // Thêm bảng vào document
            document.add(table);

            // Đóng document
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }
}
