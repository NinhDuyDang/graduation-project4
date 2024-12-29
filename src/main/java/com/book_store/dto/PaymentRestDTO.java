package com.book_store.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PaymentRestDTO implements Serializable {
    private String status;
    private String message;
    private String URL;
    public String getStatus(String ok) {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage(String successfully) {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
