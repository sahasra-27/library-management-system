package com.smartlibrary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class IssueRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Book Copy Barcode is required")
    private String barcode;

    // Constructors
    public IssueRequest() {
    }

    public IssueRequest(Long userId, String barcode) {
        this.userId = userId;
        this.barcode = barcode;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
