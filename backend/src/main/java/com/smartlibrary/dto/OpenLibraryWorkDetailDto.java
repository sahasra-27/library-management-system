package com.smartlibrary.dto;

import java.io.Serializable;
import java.util.List;

public class OpenLibraryWorkDetailDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private String title;
    private String description;
    private List<String> subjects;
    private String firstPublishDate;
    private Integer editionsCount;
    private String coverUrl;
    private List<String> authorKeys;
    private List<String> authors;
    private String isbn;

    // Constructors
    public OpenLibraryWorkDetailDto() {
    }

    public OpenLibraryWorkDetailDto(String key, String title, String description, List<String> subjects, String firstPublishDate, Integer editionsCount, String coverUrl, List<String> authorKeys, List<String> authors, String isbn) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.subjects = subjects;
        this.firstPublishDate = firstPublishDate;
        this.editionsCount = editionsCount;
        this.coverUrl = coverUrl;
        this.authorKeys = authorKeys;
        this.authors = authors;
        this.isbn = isbn;
    }

    // Getters and Setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public String getFirstPublishDate() {
        return firstPublishDate;
    }

    public void setFirstPublishDate(String firstPublishDate) {
        this.firstPublishDate = firstPublishDate;
    }

    public Integer getEditionsCount() {
        return editionsCount;
    }

    public void setEditionsCount(Integer editionsCount) {
        this.editionsCount = editionsCount;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public List<String> getAuthorKeys() {
        return authorKeys;
    }

    public void setAuthorKeys(List<String> authorKeys) {
        this.authorKeys = authorKeys;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // Builder
    public static OpenLibraryWorkDetailDtoBuilder builder() {
        return new OpenLibraryWorkDetailDtoBuilder();
    }

    public static class OpenLibraryWorkDetailDtoBuilder {
        private String key;
        private String title;
        private String description;
        private List<String> subjects;
        private String firstPublishDate;
        private Integer editionsCount;
        private String coverUrl;
        private List<String> authorKeys;
        private List<String> authors;
        private String isbn;

        OpenLibraryWorkDetailDtoBuilder() {
        }

        public OpenLibraryWorkDetailDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder subjects(List<String> subjects) {
            this.subjects = subjects;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder firstPublishDate(String firstPublishDate) {
            this.firstPublishDate = firstPublishDate;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder editionsCount(Integer editionsCount) {
            this.editionsCount = editionsCount;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder coverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder authorKeys(List<String> authorKeys) {
            this.authorKeys = authorKeys;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder authors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public OpenLibraryWorkDetailDtoBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public OpenLibraryWorkDetailDto build() {
            return new OpenLibraryWorkDetailDto(key, title, description, subjects, firstPublishDate, editionsCount, coverUrl, authorKeys, authors, isbn);
        }
    }
}
