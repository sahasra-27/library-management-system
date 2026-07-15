package com.smartlibrary.dto;

import java.io.Serializable;
import java.util.List;

public class OpenLibraryEditionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String publishDate;
    private List<String> publishers;
    private String isbn;

    // Constructors
    public OpenLibraryEditionDto() {
    }

    public OpenLibraryEditionDto(String title, String publishDate, List<String> publishers, String isbn) {
        this.title = title;
        this.publishDate = publishDate;
        this.publishers = publishers;
        this.isbn = isbn;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
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
    public static OpenLibraryEditionDtoBuilder builder() {
        return new OpenLibraryEditionDtoBuilder();
    }

    public static class OpenLibraryEditionDtoBuilder {
        private String title;
        private String publishDate;
        private List<String> publishers;
        private String isbn;

        OpenLibraryEditionDtoBuilder() {
        }

        public OpenLibraryEditionDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public OpenLibraryEditionDtoBuilder publishDate(String publishDate) {
            this.publishDate = publishDate;
            return this;
        }

        public OpenLibraryEditionDtoBuilder publishers(List<String> publishers) {
            this.publishers = publishers;
            return this;
        }

        public OpenLibraryEditionDtoBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public OpenLibraryEditionDto build() {
            return new OpenLibraryEditionDto(title, publishDate, publishers, isbn);
        }
    }
}
