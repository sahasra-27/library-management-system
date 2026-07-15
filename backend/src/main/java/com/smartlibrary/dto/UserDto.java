package com.smartlibrary.dto;

import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String status;
    private String role; // ADMIN or USER
    private String occupation;
    private String profilePhoto;
    private LocalDateTime createdAt;

    // Constructors
    public UserDto() {
    }

    public UserDto(Long id, String username, String email, String phone, String status, String role, String occupation, String profilePhoto, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.role = role;
        this.occupation = occupation;
        this.profilePhoto = profilePhoto;
        this.createdAt = createdAt;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder
    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String status;
        private String role;
        private String occupation;
        private String profilePhoto;
        private LocalDateTime createdAt;

        UserDtoBuilder() {
        }

        public UserDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDtoBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public UserDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public UserDtoBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserDtoBuilder occupation(String occupation) {
            this.occupation = occupation;
            return this;
        }

        public UserDtoBuilder profilePhoto(String profilePhoto) {
            this.profilePhoto = profilePhoto;
            return this;
        }

        public UserDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserDto build() {
            return new UserDto(id, username, email, phone, status, role, occupation, profilePhoto, createdAt);
        }
    }
}
