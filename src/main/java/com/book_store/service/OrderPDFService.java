
package com.book_store.service;
import com.book_store.entity.Order;
import com.book_store.entity.OrderDetail;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
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
import java.util.Random;

@Service
public class OrderPDFService {

    public ByteArrayOutputStream generateOrderPDF(Order order) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            // Tạo PdfWriter và PdfDocument
            PdfWriter writer = new PdfWriter(out);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A5); // Đặt khổ giấy A5

            // Tạo Document
            Document document = new Document(pdfDoc);
            document.setMargins(15, 15, 15, 15);

            // Nạp font hỗ trợ Unicode
            String fontPath = "C:/Windows/Fonts/arial.ttf"; // Đường dẫn tới font Arial
            PdfFont font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, true);
            document.setFont(font);

            // Tiêu đề công ty
            document.add(new Paragraph("Công ty cổ phần sách BookLibrary Hà Nội")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Số 1 Đại Cồ Việt, Hai Bà Trưng, Hà Nội, Việt Nam")
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // Tiêu đề hóa đơn
            document.add(new Paragraph("Hóa đơn bán hàng")
                    .setBold()
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5));

            // Số hóa đơn
            String invoiceNumber = generateRandomInvoiceNumber(10);
            document.add(new Paragraph("Số hóa đơn: " + invoiceNumber)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10));


            // Thông tin đơn hàng
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            document.add(new Paragraph("Mã đơn hàng: " + order.getId()).setFontSize(9));
            document.add(new Paragraph("Ngày tạo: " + order.getCreatedAt().toString()).setFontSize(9));
            document.add(new Paragraph("Tên khách hàng: " + order.getCustomer().getName()).setFontSize(9));
            document.add(new Paragraph("Phương thức thanh toán: " + (order.getPaymentMethod() == 0 ? "Ship cod" : "Banking")).setFontSize(9));
            document.add(new Paragraph("Tên người nhận: " + order.getReceiverName()).setFontSize(9));
            document.add(new Paragraph("Số điện thoại: " + order.getReceiverPhone()).setFontSize(9));
            document.add(new Paragraph("Địa chỉ: " + order.getReceiverAddress()).setFontSize(9));
            document.add(new Paragraph("Mã giảm giá: " + (order.getPromotion() == null ? "Không có mã giảm giá" : order.getPromotion().getName())).setFontSize(9));
            document.add(new Paragraph("Đơn vị vận chuyển: " + order.getShippingUnit().getName()).setFontSize(9));
            document.add(new Paragraph("Trạng thái thanh toán: " + (order.getPaymentStatus() == 0 ? "Chưa thanh toán" : "Đã thanh toán")).setFontSize(9));
            document.add(new Paragraph("Trạng thái: " +
                    (order.getStatus() == 0 ? "Đang xử lý" :
                            (order.getStatus() == 1 ? "Đang vận chuyển" :
                                    (order.getStatus() == 2 ? "Đã hoàn thành" :
                                            (order.getStatus() == 3 ? "Đã hủy" : "Không xác định")))))
                    .setFontSize(9));
            document.add(new Paragraph("Tổng giá trị: " + formatter.format(order.getPrice()) + " VND").setFontSize(9).setMarginBottom(10));

            // Tạo bảng sản phẩm
            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            table.addHeaderCell("Tên sản phẩm").setFontSize(9);
            table.addHeaderCell("Số lượng").setFontSize(9);
            table.addHeaderCell("Giá (VND)").setFontSize(9);
            table.addHeaderCell("Thành tiền (VND)").setFontSize(9);

            for (OrderDetail item : order.getOrderDetails()) {
                table.addCell(new Paragraph(item.getProduct().getName()).setFontSize(9));
                table.addCell(new Paragraph(String.valueOf(item.getQuantity())).setFontSize(9));
                table.addCell(new Paragraph(formatter.format(item.getPrice())).setFontSize(9));
                table.addCell(new Paragraph(formatter.format(new BigDecimal(item.getQuantity()).multiply(item.getPrice()))).setFontSize(9));
            }

            document.add(table.setMarginBottom(10));

            // Chữ ký và lời cảm ơn
            document.add(new Paragraph("Người lập hóa đơn")
                    .setBold()
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT));
            document.add(new Paragraph("(Ký, ghi rõ họ tên)")
                    .setItalic()
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginBottom(5));
            document.add(new Paragraph("Trân trọng cảm ơn quý khách hàng!")
                    .setBold()
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10));

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    private String generateRandomInvoiceNumber(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}

