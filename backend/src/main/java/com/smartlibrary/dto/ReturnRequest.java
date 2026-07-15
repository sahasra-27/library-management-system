package com.smartlibrary.dto;

import jakarta.validation.constraints.NotBlank;

public class ReturnRequest {
    @NotBlank(message = "Book Copy Barcode is required")
    private String barcode;

    private String bookCondition;

    // Constructors
    public ReturnRequest() {
    }

    public ReturnRequest(String barcode, String bookCondition) {
        this.barcode = barcode;
        this.bookCondition = bookCondition;
    }

    // Getters and Setters
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBookCondition() {
        return bookCondition;
    }

    public void setBookCondition(String bookCondition) {
        this.bookCondition = bookCondition;
    }
}
