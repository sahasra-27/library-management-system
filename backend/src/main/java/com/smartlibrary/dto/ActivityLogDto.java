package com.smartlibrary.dto;

import java.time.LocalDateTime;

public class ActivityLogDto {
    private Long id;
    private String username;
    private String action;
    private String details;
    private LocalDateTime timestamp;

    // Constructors
    public ActivityLogDto() {
    }

    public ActivityLogDto(Long id, String username, String action, String details, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
    public static ActivityLogDtoBuilder builder() {
        return new ActivityLogDtoBuilder();
    }

    public static class ActivityLogDtoBuilder {
        private Long id;
        private String username;
        private String action;
        private String details;
        private LocalDateTime timestamp;

        ActivityLogDtoBuilder() {
        }

        public ActivityLogDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ActivityLogDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public ActivityLogDtoBuilder action(String action) {
            this.action = action;
            return this;
        }

        public ActivityLogDtoBuilder details(String details) {
            this.details = details;
            return this;
        }

        public ActivityLogDtoBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ActivityLogDto build() {
            return new ActivityLogDto(id, username, action, details, timestamp);
        }
    }
}
