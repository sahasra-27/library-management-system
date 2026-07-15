package com.smartlibrary.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "books", uniqueConstraints = {
    @UniqueConstraint(columnNames = "isbn")
})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 255)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 50)
    private String language;

    @Column(length = 50)
    private String edition;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "shelf_number", length = 50)
    private String shelfNumber;

    @Column(name = "rack_number", length = 50)
    private String rackNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "number_of_pages")
    private Integer numberOfPages;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "page_count")
    private Integer pageCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BookStatus {
        ACTIVE,
        DELETED
    }

    // Constructors
    public Book() {
    }

    public Book(Long id, String isbn, String title, Author author, Publisher publisher, Category category, String language, String edition, Integer publicationYear, BigDecimal price, String shelfNumber, String rackNumber, Integer quantity, Integer availableQuantity, String description, String coverImage, String subtitle, Integer numberOfPages, String coverImageUrl, Integer pageCount, BookStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
        this.language = language;
        this.edition = edition;
        this.publicationYear = publicationYear;
        this.price = price;
        this.shelfNumber = shelfNumber;
        this.rackNumber = rackNumber;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.description = description;
        this.coverImage = coverImage;
        this.subtitle = subtitle;
        this.numberOfPages = numberOfPages;
        this.coverImageUrl = coverImageUrl;
        this.pageCount = pageCount;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(String shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    public String getRackNumber() {
        return rackNumber;
    }

    public void setRackNumber(String rackNumber) {
        this.rackNumber = rackNumber;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Builder
    public static BookBuilder builder() {
        return new BookBuilder();
    }

    public static class BookBuilder {
        private Long id;
        private String isbn;
        private String title;
        private Author author;
        private Publisher publisher;
        private Category category;
        private String language;
        private String edition;
        private Integer publicationYear;
        private BigDecimal price;
        private String shelfNumber;
        private String rackNumber;
        private Integer quantity;
        private Integer availableQuantity;
        private String description;
        private String coverImage;
        private String subtitle;
        private Integer numberOfPages;
        private String coverImageUrl;
        private Integer pageCount;
        private BookStatus status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        BookBuilder() {
        }

        public BookBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public BookBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BookBuilder author(Author author) {
            this.author = author;
            return this;
        }

        public BookBuilder publisher(Publisher publisher) {
            this.publisher = publisher;
            return this;
        }

        public BookBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public BookBuilder language(String language) {
            this.language = language;
            return this;
        }

        public BookBuilder edition(String edition) {
            this.edition = edition;
            return this;
        }

        public BookBuilder publicationYear(Integer publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        public BookBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public BookBuilder shelfNumber(String shelfNumber) {
            this.shelfNumber = shelfNumber;
            return this;
        }

        public BookBuilder rackNumber(String rackNumber) {
            this.rackNumber = rackNumber;
            return this;
        }

        public BookBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public BookBuilder availableQuantity(Integer availableQuantity) {
            this.availableQuantity = availableQuantity;
            return this;
        }

        public BookBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BookBuilder coverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public BookBuilder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public BookBuilder numberOfPages(Integer numberOfPages) {
            this.numberOfPages = numberOfPages;
            return this;
        }

        public BookBuilder coverImageUrl(String coverImageUrl) {
            this.coverImageUrl = coverImageUrl;
            return this;
        }

        public BookBuilder pageCount(Integer pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public BookBuilder status(BookStatus status) {
            this.status = status;
            return this;
        }

        public BookBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BookBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Book build() {
            return new Book(id, isbn, title, author, publisher, category, language, edition, publicationYear, price, shelfNumber, rackNumber, quantity, availableQuantity, description, coverImage, subtitle, numberOfPages, coverImageUrl, pageCount, status, createdAt, updatedAt);
        }
    }
}
