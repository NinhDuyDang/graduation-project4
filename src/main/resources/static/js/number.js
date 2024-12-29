document.addEventListener("DOMContentLoaded", function() {
    // Lấy tất cả các phần tử với class 'price'
    var priceElements = document.querySelectorAll(".price");

    priceElements.forEach(function(priceElement) {
        // Lấy giá trị từ thuộc tính data-price
        var price = priceElement.getAttribute("data-price");

        // Chuyển giá trị thành số nguyên
        price = Number(price);

        // Kiểm tra nếu giá trị là số hợp lệ
        if (!isNaN(price)) {
            // Định dạng số với dấu phân cách hàng nghìn
            var formattedPrice = price.toLocaleString('de-DE') + ' VND';

            // Cập nhật nội dung của thẻ h2
            priceElement.textContent = formattedPrice;
        } else {
            // Nếu không phải số hợp lệ, hiển thị một thông báo lỗi
            priceElement.textContent = "Invalid price";
        }
    });
});