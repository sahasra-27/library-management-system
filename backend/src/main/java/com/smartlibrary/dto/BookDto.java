package com.smartlibrary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookDto {
    private Long id;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Author ID is required")
    private Long authorId;
    private String authorName;

    @NotNull(message = "Publisher ID is required")
    private Long publisherId;
    private String publisherName;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
    private String categoryName;

    private String language;
    private String edition;
    private Integer publicationYear;
    private BigDecimal price;
    private String shelfNumber;
    private String rackNumber;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    private Integer availableQuantity;
    private String description;
    private String coverImage;
    private String subtitle;
    private Integer numberOfPages;
    private String coverImageUrl;
    private Integer pageCount;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public BookDto() {
    }

    public BookDto(Long id, String isbn, String title, Long authorId, String authorName, Long publisherId, String publisherName, Long categoryId, String categoryName, String language, String edition, Integer publicationYear, BigDecimal price, String shelfNumber, String rackNumber, Integer quantity, Integer availableQuantity, String description, String coverImage, String subtitle, Integer numberOfPages, String coverImageUrl, Integer pageCount, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.authorId = authorId;
        this.authorName = authorName;
        this.publisherId = publisherId;
        this.publisherName = publisherName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
    public static BookDtoBuilder builder() {
        return new BookDtoBuilder();
    }

    public static class BookDtoBuilder {
        private Long id;
        private String isbn;
        private String title;
        private Long authorId;
        private String authorName;
        private Long publisherId;
        private String publisherName;
        private Long categoryId;
        private String categoryName;
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
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        BookDtoBuilder() {
        }

        public BookDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BookDtoBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public BookDtoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BookDtoBuilder authorId(Long authorId) {
            this.authorId = authorId;
            return this;
        }

        public BookDtoBuilder authorName(String authorName) {
            this.authorName = authorName;
            return this;
        }

        public BookDtoBuilder publisherId(Long publisherId) {
            this.publisherId = publisherId;
            return this;
        }

        public BookDtoBuilder publisherName(String publisherName) {
            this.publisherName = publisherName;
            return this;
        }

        public BookDtoBuilder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public BookDtoBuilder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public BookDtoBuilder language(String language) {
            this.language = language;
            return this;
        }

        public BookDtoBuilder edition(String edition) {
            this.edition = edition;
            return this;
        }

        public BookDtoBuilder publicationYear(Integer publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        public BookDtoBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public BookDtoBuilder shelfNumber(String shelfNumber) {
            this.shelfNumber = shelfNumber;
            return this;
        }

        public BookDtoBuilder rackNumber(String rackNumber) {
            this.rackNumber = rackNumber;
            return this;
        }

        public BookDtoBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public BookDtoBuilder availableQuantity(Integer availableQuantity) {
            this.availableQuantity = availableQuantity;
            return this;
        }

        public BookDtoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BookDtoBuilder coverImage(String coverImage) {
            this.coverImage = coverImage;
            return this;
        }

        public BookDtoBuilder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public BookDtoBuilder numberOfPages(Integer numberOfPages) {
            this.numberOfPages = numberOfPages;
            return this;
        }

        public BookDtoBuilder coverImageUrl(String coverImageUrl) {
            this.coverImageUrl = coverImageUrl;
            return this;
        }

        public BookDtoBuilder pageCount(Integer pageCount) {
            this.pageCount = pageCount;
            return this;
        }

        public BookDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public BookDtoBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BookDtoBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BookDto build() {
            return new BookDto(id, isbn, title, authorId, authorName, publisherId, publisherName, categoryId, categoryName, language, edition, publicationYear, price, shelfNumber, rackNumber, quantity, availableQuantity, description, coverImage, subtitle, numberOfPages, coverImageUrl, pageCount, status, createdAt, updatedAt);
        }
    }
}
