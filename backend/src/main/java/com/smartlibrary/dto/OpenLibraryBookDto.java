package com.smartlibrary.dto;

import java.io.Serializable;
import java.util.List;

public class OpenLibraryBookDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String key;
    private String title;
    private List<String> authors;
    private String coverUrl;
    private Integer firstPublishYear;
    private List<String> publishers;
    private String isbn;

    // Constructors
    public OpenLibraryBookDto() {
    }

    public OpenLibraryBookDto(String key, String title, List<String> authors, String coverUrl, Integer firstPublishYear, List<String> publishers, String isbn) {
        this.key = key;
        this.title = title;
        this.authors = authors;
        this.coverUrl = coverUrl;
        this.firstPublishYear = firstPublishYear;
        this.publishers = publishers;
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

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Integer getFirstPublishYear() {
        return firstPublishYear;
    }

    public void setFirstPublishYear(Integer firstPublishYear) {
        this.firstPublishYear = firstPublishYear;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    // Builder
    public static OpenLibraryBookDtoBuilder builder() {
        return new OpenLibraryBookDtoBuilder();
    }

    public static class OpenLibraryBookDtoBuilder {
        private String key;
        private String title;
        private List<String> authors;
        private String coverUrl;
        private Integer firstPublishYear;
        private List<String> publishers;
        private String isbn;

        OpenLibraryBookDtoBuilder() {
        }

        public OpenLibraryBookDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        public OpenLibraryBookDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public OpenLibraryBookDtoBuilder authors(List<String> authors) {
            this.authors = authors;
            return this;
        }

        public OpenLibraryBookDtoBuilder coverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
            return this;
        }

        public OpenLibraryBookDtoBuilder firstPublishYear(Integer firstPublishYear) {
            this.firstPublishYear = firstPublishYear;
            return this;
        }

        public OpenLibraryBookDtoBuilder publishers(List<String> publishers) {
            this.publishers = publishers;
            return this;
        }

        public OpenLibraryBookDtoBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public OpenLibraryBookDto build() {
            return new OpenLibraryBookDto(key, title, authors, coverUrl, firstPublishYear, publishers, isbn);
        }
    }
}
