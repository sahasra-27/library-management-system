package com.smartlibrary.dto;

public class UserSessionDto {
    private Long id;
    private String username;
    private String email;
    private String role; // ADMIN or USER
    private String occupation;
    private String token; // Authentication token

    // Constructors
    public UserSessionDto() {
    }

    public UserSessionDto(Long id, String username, String email, String role, String occupation, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.occupation = occupation;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Builder
    public static UserSessionDtoBuilder builder() {
        return new UserSessionDtoBuilder();
    }

    public static class UserSessionDtoBuilder {
        private Long id;
        private String username;
        private String email;
        private String role;
        private String occupation;
        private String token;

        UserSessionDtoBuilder() {
        }

        public UserSessionDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserSessionDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserSessionDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserSessionDtoBuilder role(String role) {
            this.role = role;
            return this;
        }

        public UserSessionDtoBuilder occupation(String occupation) {
            this.occupation = occupation;
            return this;
        }

        public UserSessionDtoBuilder token(String token) {
            this.token = token;
            return this;
        }

        public UserSessionDto build() {
            return new UserSessionDto(id, username, email, role, occupation, token);
        }
    }
}
