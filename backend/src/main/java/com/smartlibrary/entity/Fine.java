package com.smartlibrary.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fines")
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "issue_id", nullable = true)
    private IssuedBook issuedBook;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason")
    private String reason;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FineStatus status;

    @Column(name = "paid_date")
    private LocalDateTime paidDate;

    public enum FineStatus {
        UNPAID,
        PAID,
        OVERDUE
    }

    // Constructors
    public Fine() {
    }

    public Fine(Long id, IssuedBook issuedBook, User user, BigDecimal amount, String reason, LocalDateTime dueDate, FineStatus status, LocalDateTime paidDate) {
        this.id = id;
        this.issuedBook = issuedBook;
        this.user = user;
        this.amount = amount;
        this.reason = reason;
        this.dueDate = dueDate;
        this.status = status;
        this.paidDate = paidDate;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public FineStatus getStatus() {
        return status;
    }

    public void setStatus(FineStatus status) {
        this.status = status;
    }

    public LocalDateTime getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(LocalDateTime paidDate) {
        this.paidDate = paidDate;
    }

    // Builder
    public static FineBuilder builder() {
        return new FineBuilder();
    }

    public static class FineBuilder {
        private Long id;
        private IssuedBook issuedBook;
        private User user;
        private BigDecimal amount;
        private String reason;
        private LocalDateTime dueDate;
        private FineStatus status;
        private LocalDateTime paidDate;

        FineBuilder() {
        }

        public FineBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public FineBuilder issuedBook(IssuedBook issuedBook) {
            this.issuedBook = issuedBook;
            return this;
        }

        public FineBuilder user(User user) {
            this.user = user;
            return this;
        }

        public FineBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public FineBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public FineBuilder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public FineBuilder status(FineStatus status) {
            this.status = status;
            return this;
        }

        public FineBuilder paidDate(LocalDateTime paidDate) {
            this.paidDate = paidDate;
            return this;
        }

        public Fine build() {
            return new Fine(id, issuedBook, user, amount, reason, dueDate, status, paidDate);
        }
    }
}
