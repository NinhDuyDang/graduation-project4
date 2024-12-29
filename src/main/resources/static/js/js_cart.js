function increaseAmount(cid, price) {
    if (parseInt(document.getElementById(cid).value) < parseInt(document.getElementById('maxQuantity' + cid).value)) {
        var quantity = parseInt(document.getElementById(cid).value) + 1;
        document.getElementById(cid).value = parseInt(quantity);
        var newprice = (parseInt(document.getElementById(cid).value)) * parseFloat(price);
        newprice = Number(newprice);
        var formattedPrice = newprice.toLocaleString('de-DE') + ' VND';

        document.getElementById('total' + cid).innerHTML = formattedPrice;
    } else {
        window.alert("Hết số lượng tối đa");
    }
    jQuery.ajax({
        type: 'post',
        url: "/cart/increaseAmount",
        data: {
            cartId: cid
        },
        success: function () {
        }, error: function () {
        }
    });
}

function reduceAmount(cid, price) {
    var quantity = parseInt(document.getElementById(cid).value);
    if (quantity !== 1) {
        document.getElementById(cid).value = parseInt(quantity - 1);
        var newprice = (parseInt(document.getElementById(cid).value)) * parseFloat(price);
        newprice = Number(newprice);
        var formattedPrice = newprice.toLocaleString('de-DE') + ' VND';

        document.getElementById('total' + cid).innerHTML = formattedPrice;
    }
    jQuery.ajax({
        url: "/cart/reduceAmount",
        type: "POST",
        data: {
            cartId: cid
        },
        success: function () {
        }, error: function () {
        }
    });
}

function ChooseProduct(cid, isChecked) {
    var status = isChecked ? 1 : 0;
    jQuery.ajax({
        url: "/cart/ChooseProduct",
        type: "POST",
        data: {
            cartId: cid,
            status: status
        },
        success: function () {
        }, error: function () {
        }
    });
}

