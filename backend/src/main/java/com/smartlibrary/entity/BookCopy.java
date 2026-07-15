package com.smartlibrary.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_copies", uniqueConstraints = {
    @UniqueConstraint(columnNames = "barcode")
})
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false, unique = true, length = 100)
    private String barcode;

    @Column(name = "copy_number", nullable = false)
    private Integer copyNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CopyStatus status;

    public enum CopyStatus {
        AVAILABLE,
        BORROWED,
        LOST
    }

    // Constructors
    public BookCopy() {
    }

    public BookCopy(Long id, Book book, String barcode, Integer copyNumber, CopyStatus status) {
        this.id = id;
        this.book = book;
        this.barcode = barcode;
        this.copyNumber = copyNumber;
        this.status = status;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Integer getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(Integer copyNumber) {
        this.copyNumber = copyNumber;
    }

    public CopyStatus getStatus() {
        return status;
    }

    public void setStatus(CopyStatus status) {
        this.status = status;
    }

    // Builder
    public static BookCopyBuilder builder() {
        return new BookCopyBuilder();
    }

    public static class BookCopyBuilder {
        private Long id;
        private Book book;
        private String barcode;
        private Integer copyNumber;
        private CopyStatus status;

        BookCopyBuilder() {
        }

        public BookCopyBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookCopyBuilder book(Book book) {
            this.book = book;
            return this;
        }

        public BookCopyBuilder barcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public BookCopyBuilder copyNumber(Integer copyNumber) {
            this.copyNumber = copyNumber;
            return this;
        }

        public BookCopyBuilder status(CopyStatus status) {
            this.status = status;
            return this;
        }

        public BookCopy build() {
            return new BookCopy(id, book, barcode, copyNumber, status);
        }
    }
}
