package com.smartlibrary.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_images")
public class BookImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    // Constructors
    public BookImage() {
    }

    public BookImage(Long id, Book book, String imageUrl) {
        this.id = id;
        this.book = book;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Builder
    public static BookImageBuilder builder() {
        return new BookImageBuilder();
    }

    public static class BookImageBuilder {
        private Long id;
        private Book book;
        private String imageUrl;

        BookImageBuilder() {
        }

        public BookImageBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookImageBuilder book(Book book) {
            this.book = book;
            return this;
        }

        public BookImageBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public BookImage build() {
            return new BookImage(id, book, imageUrl);
        }
    }
}
