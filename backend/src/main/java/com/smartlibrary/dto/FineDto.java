package com.smartlibrary.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FineDto {
    private Long id;
    private Long issueId;
    private Long userId;
    private String username;
    private String bookTitle;
    private String barcode;
    private BigDecimal amount;
    private String status;
    private LocalDateTime paidDate;
    private LocalDateTime issueDate;
    private LocalDateTime returnDate;
    private LocalDateTime dueDate;
    private Long daysLate;
    private String reason;

    // Constructors
    public FineDto() {
    }

    public FineDto(Long id, Long issueId, Long userId, String username, String bookTitle, String barcode, BigDecimal amount, String status, LocalDateTime paidDate, LocalDateTime issueDate, LocalDateTime returnDate, LocalDateTime dueDate, Long daysLate, String reason) {
        this.id = id;
        this.issueId = issueId;
        this.userId = userId;
        this.username = username;
        this.bookTitle = bookTitle;
        this.barcode = barcode;
        this.amount = amount;
        this.status = status;
        this.paidDate = paidDate;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.dueDate = dueDate;
        this.daysLate = daysLate;
        this.reason = reason;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Long getDaysLate() {
        return daysLate;
    }

    public void setDaysLate(Long daysLate) {
        this.daysLate = daysLate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // Builder
    public static FineDtoBuilder builder() {
        return new FineDtoBuilder();
    }

    public static class FineDtoBuilder {
        private Long id;
        private Long issueId;
        private Long userId;
        private String username;
        private String bookTitle;
        private String barcode;
        private BigDecimal amount;
        private String status;
        private LocalDateTime paidDate;
        private LocalDateTime issueDate;
        private LocalDateTime returnDate;
        private LocalDateTime dueDate;
        private Long daysLate;
        private String reason;

        FineDtoBuilder() {
        }

        public FineDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FineDtoBuilder issueId(Long issueId) {
            this.issueId = issueId;
            return this;
        }

        public FineDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public FineDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public FineDtoBuilder bookTitle(String bookTitle) {
            this.bookTitle = bookTitle;
            return this;
        }

        public FineDtoBuilder barcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public FineDtoBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public FineDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public FineDtoBuilder paidDate(LocalDateTime paidDate) {
            this.paidDate = paidDate;
            return this;
        }

        public FineDtoBuilder issueDate(LocalDateTime issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public FineDtoBuilder returnDate(LocalDateTime returnDate) {
            this.returnDate = returnDate;
            return this;
        }

        public FineDtoBuilder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public FineDtoBuilder daysLate(Long daysLate) {
            this.daysLate = daysLate;
            return this;
        }

        public FineDtoBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public FineDto build() {
            return new FineDto(id, issueId, userId, username, bookTitle, barcode, amount, status, paidDate, issueDate, returnDate, dueDate, daysLate, reason);
        }
    }
}
