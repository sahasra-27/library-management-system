package com.smartlibrary.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "returned_books")
public class ReturnedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issue_id", nullable = false, unique = true)
    private IssuedBook issuedBook;

    @Column(name = "return_date", nullable = false)
    private LocalDateTime returnDate;

    @Column(name = "book_condition", length = 100)
    private String bookCondition;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processed_by")
    private User processedBy;

    // Constructors
    public ReturnedBook() {
    }

    public ReturnedBook(Long id, IssuedBook issuedBook, LocalDateTime returnDate, String bookCondition, User processedBy) {
        this.id = id;
        this.issuedBook = issuedBook;
        this.returnDate = returnDate;
        this.bookCondition = bookCondition;
        this.processedBy = processedBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IssuedBook getIssuedBook() {
        return issuedBook;
    }

    public void setIssuedBook(IssuedBook issuedBook) {
        this.issuedBook = issuedBook;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getBookCondition() {
        return bookCondition;
    }

    public void setBookCondition(String bookCondition) {
        this.bookCondition = bookCondition;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    // Builder
    public static ReturnedBookBuilder builder() {
        return new ReturnedBookBuilder();
    }

    public static class ReturnedBookBuilder {
        private Long id;
        private IssuedBook issuedBook;
        private LocalDateTime returnDate;
        private String bookCondition;
        private User processedBy;

        ReturnedBookBuilder() {
        }

        public ReturnedBookBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReturnedBookBuilder issuedBook(IssuedBook issuedBook) {
            this.issuedBook = issuedBook;
            return this;
        }

        public ReturnedBookBuilder returnDate(LocalDateTime returnDate) {
            this.returnDate = returnDate;
            return this;
        }

        public ReturnedBookBuilder bookCondition(String bookCondition) {
            this.bookCondition = bookCondition;
            return this;
        }

        public ReturnedBookBuilder processedBy(User processedBy) {
            this.processedBy = processedBy;
            return this;
        }

        public ReturnedBook build() {
            return new ReturnedBook(id, issuedBook, returnDate, bookCondition, processedBy);
        }
    }
}
