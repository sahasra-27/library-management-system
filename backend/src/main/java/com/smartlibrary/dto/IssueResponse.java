package com.smartlibrary.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class IssueResponse {
    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private Long bookId;
    private String bookTitle;
    private String barcode;
    private LocalDateTime issueDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status;
    private BigDecimal fineAmount;
    private String bookCondition;
    private String authorName;
    private String isbn;

    // Constructors
    public IssueResponse() {
    }

    public IssueResponse(Long id, Long userId, String username, String userEmail, Long bookId, String bookTitle, String barcode, LocalDateTime issueDate, LocalDateTime dueDate, LocalDateTime returnDate, String status, BigDecimal fineAmount, String bookCondition, String authorName, String isbn) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.userEmail = userEmail;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.barcode = barcode;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fineAmount = fineAmount;
        this.bookCondition = bookCondition;
        this.authorName = authorName;
        this.isbn = isbn;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
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

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getBookCondition() {
        return bookCondition;
    }

    public void setBookCondition(String bookCondition) {
        this.bookCondition = bookCondition;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // Builder
    public static IssueResponseBuilder builder() {
        return new IssueResponseBuilder();
    }

    public static class IssueResponseBuilder {
        private Long id;
        private Long userId;
        private String username;
        private String userEmail;
        private Long bookId;
        private String bookTitle;
        private String barcode;
        private LocalDateTime issueDate;
        private LocalDateTime dueDate;
        private LocalDateTime returnDate;
        private String status;
        private BigDecimal fineAmount;
        private String bookCondition;
        private String authorName;
        private String isbn;

        IssueResponseBuilder() {
        }

        public IssueResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public IssueResponseBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public IssueResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public IssueResponseBuilder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public IssueResponseBuilder bookId(Long bookId) {
            this.bookId = bookId;
            return this;
        }

        public IssueResponseBuilder bookTitle(String bookTitle) {
            this.bookTitle = bookTitle;
            return this;
        }

        public IssueResponseBuilder barcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public IssueResponseBuilder issueDate(LocalDateTime issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public IssueResponseBuilder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public IssueResponseBuilder returnDate(LocalDateTime returnDate) {
            this.returnDate = returnDate;
            return this;
        }

        public IssueResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        public IssueResponseBuilder fineAmount(BigDecimal fineAmount) {
            this.fineAmount = fineAmount;
            return this;
        }

        public IssueResponseBuilder bookCondition(String bookCondition) {
            this.bookCondition = bookCondition;
            return this;
        }

        public IssueResponseBuilder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public IssueResponseBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public IssueResponse build() {
            return new IssueResponse(id, userId, username, userEmail, bookId, bookTitle, barcode, issueDate, dueDate, returnDate, status, fineAmount, bookCondition, authorName, isbn);
        }
    }
}
