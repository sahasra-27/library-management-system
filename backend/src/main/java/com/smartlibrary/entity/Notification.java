package com.smartlibrary.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 50)
    private String type; // e.g. ISSUE_SUCCESS, RETURN_REMINDER, DUE_REMINDER, FINE_REMINDER, RESERVATION_AVAILABLE, NEW_BOOK_ADDED

    @Column(name = "read_status", nullable = false)
    private boolean readStatus = false;

    @CreationTimestamp
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    // Constructors
    public Notification() {
    }

    public Notification(Long id, User user, String message, String type, boolean readStatus, LocalDateTime sentAt) {
        this.id = id;
        this.user = user;
        this.message = message;
        this.type = type;
        this.readStatus = readStatus;
        this.sentAt = sentAt;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    // Builder
    public static NotificationBuilder builder() {
        return new NotificationBuilder();
    }

    public static class NotificationBuilder {
        private Long id;
        private User user;
        private String message;
        private String type;
        private boolean readStatus = false; // Builder default
        private LocalDateTime sentAt;

        NotificationBuilder() {
        }

        public NotificationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public NotificationBuilder user(User user) {
            this.user = user;
            return this;
        }

        public NotificationBuilder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationBuilder type(String type) {
            this.type = type;
            return this;
        }

        public NotificationBuilder readStatus(boolean readStatus) {
            this.readStatus = readStatus;
            return this;
        }

        public NotificationBuilder sentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
            return this;
        }

        public Notification build() {
            return new Notification(id, user, message, type, readStatus, sentAt);
        }
    }
}
