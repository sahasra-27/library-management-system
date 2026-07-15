package com.smartlibrary.dto;

import java.io.Serializable;

public class OpenLibraryAuthorDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private String name;
    private String biography;
    private String birthDate;
    private String deathDate;
    private String photoUrl;

    // Constructors
    public OpenLibraryAuthorDto() {
    }

    public OpenLibraryAuthorDto(String key, String name, String biography, String birthDate, String deathDate, String photoUrl) {
        this.key = key;
        this.name = name;
        this.biography = biography;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.photoUrl = photoUrl;
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    // Builder
    public static OpenLibraryAuthorDtoBuilder builder() {
        return new OpenLibraryAuthorDtoBuilder();
    }

    public static class OpenLibraryAuthorDtoBuilder {
        private String key;
        private String name;
        private String biography;
        private String birthDate;
        private String deathDate;
        private String photoUrl;

        OpenLibraryAuthorDtoBuilder() {
        }

        public OpenLibraryAuthorDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        public OpenLibraryAuthorDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        public OpenLibraryAuthorDtoBuilder biography(String biography) {
            this.biography = biography;
            return this;
        }

        public OpenLibraryAuthorDtoBuilder birthDate(String birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public OpenLibraryAuthorDtoBuilder deathDate(String deathDate) {
            this.deathDate = deathDate;
            return this;
        }

        public OpenLibraryAuthorDtoBuilder photoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public OpenLibraryAuthorDto build() {
            return new OpenLibraryAuthorDto(key, name, biography, birthDate, deathDate, photoUrl);
        }
    }
}
