package com.smartlibrary.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 100)
    private String action; // e.g. LOGIN, LOGOUT, BOOK_ADDED, BOOK_UPDATED, BOOK_DELETED, BOOK_ISSUED, BOOK_RETURNED, FINE_PAID

    @Column(columnDefinition = "TEXT")
    private String details;

    @CreationTimestamp
    @Column(name = "timestamp", updatable = false)
    private LocalDateTime timestamp;

    // Constructors
    public ActivityLog() {
    }

    public ActivityLog(Long id, User user, String action, String details, LocalDateTime timestamp) {
        this.id = id;
        this.user = user;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Builder
    public static ActivityLogBuilder builder() {
        return new ActivityLogBuilder();
    }

    public static class ActivityLogBuilder {
        private Long id;
        private User user;
        private String action;
        private String details;
        private LocalDateTime timestamp;

        ActivityLogBuilder() {
        }

        public ActivityLogBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ActivityLogBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ActivityLogBuilder action(String action) {
            this.action = action;
            return this;
        }

        public ActivityLogBuilder details(String details) {
            this.details = details;
            return this;
        }

        public ActivityLogBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ActivityLog build() {
            return new ActivityLog(id, user, action, details, timestamp);
        }
    }
}
