package com.smartlibrary.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "issued_books")
public class IssuedBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_copy_id", nullable = false)
    private BookCopy bookCopy;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IssueStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum IssueStatus {
        ISSUED,
        RETURNED,
        OVERDUE,
        LOST
    }

    // Constructors
    public IssuedBook() {
    }

    public IssuedBook(Long id, User user, BookCopy bookCopy, LocalDateTime issueDate, LocalDateTime dueDate, IssueStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.bookCopy = bookCopy;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public void setBookCopy(BookCopy bookCopy) {
        this.bookCopy = bookCopy;
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

    public IssueStatus getStatus() {
        return status;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder
    public static IssuedBookBuilder builder() {
        return new IssuedBookBuilder();
    }

    public static class IssuedBookBuilder {
        private Long id;
        private User user;
        private BookCopy bookCopy;
        private LocalDateTime issueDate;
        private LocalDateTime dueDate;
        private IssueStatus status;
        private LocalDateTime createdAt;

        IssuedBookBuilder() {
        }

        public IssuedBookBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public IssuedBookBuilder user(User user) {
            this.user = user;
            return this;
        }

        public IssuedBookBuilder bookCopy(BookCopy bookCopy) {
            this.bookCopy = bookCopy;
            return this;
        }

        public IssuedBookBuilder issueDate(LocalDateTime issueDate) {
            this.issueDate = issueDate;
            return this;
        }

        public IssuedBookBuilder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public IssuedBookBuilder status(IssueStatus status) {
            this.status = status;
            return this;
        }

        public IssuedBookBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public IssuedBook build() {
            return new IssuedBook(id, user, bookCopy, issueDate, dueDate, status, createdAt);
        }
    }
}
