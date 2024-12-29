$(document).ready(function () {
    $("#email").on("input", function (g) {
        $('#msg_email').hide();
        if ($('#email').val() == null || $('#email').val() == "") {
            $('#msg_email').show();
            $("#msg_email").html("Bắt buộc nhập Email.").css("color", "red");
        } else {
            $.ajax({
                type: 'post',
                url: "/check_email",
                data: JSON.stringify({email: $('#email').val()}),
                contentType: 'application/json; charset=utf-8',
                // dataType: 'json',
                cache: false,
                beforeSend: function (h) {
                    $('#msg_email').show();
                    $('#msg_email').html('Checking...');
                },
                statusCode: {
                    400: function (xhr) {
                        $('#msg_email').show();
                        $("#msg_email").html("Email không tồn tại!!!").css("color", "red");
                    }
                },
                success: function (msg_email) {
                    $('#msg_email').show();
                    if (msg_email !== null || msg_email !== "") {
                        $("#msg_email").hide();
                    } else {
                        $("#msg_email").html("Email không tồn tại!!!").css("color", "red");
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $('#msg_email').show();
                    $("#msg_email").html(textStatus + " " + errorThrown).css("color", "red");
                }
            });
        }
    });
});
