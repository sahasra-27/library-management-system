package com.smartlibrary.dto;

import java.time.LocalDateTime;

public class ReservationDto {
    private Long id;
    private Long userId;
    private String username;
    private Long bookId;
    private String bookTitle;
    private String isbn;
    private LocalDateTime reservationDate;
    private LocalDateTime expiryDate;
    private Integer queuePosition;
    private String status;

    // Constructors
    public ReservationDto() {
    }

    public ReservationDto(Long id, Long userId, String username, Long bookId, String bookTitle, String isbn, LocalDateTime reservationDate, LocalDateTime expiryDate, Integer queuePosition, String status) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.isbn = isbn;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.queuePosition = queuePosition;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Builder
    public static ReservationDtoBuilder builder() {
        return new ReservationDtoBuilder();
    }

    public static class ReservationDtoBuilder {
        private Long id;
        private Long userId;
        private String username;
        private Long bookId;
        private String bookTitle;
        private String isbn;
        private LocalDateTime reservationDate;
        private LocalDateTime expiryDate;
        private Integer queuePosition;
        private String status;

        ReservationDtoBuilder() {
        }

        public ReservationDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReservationDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ReservationDtoBuilder username(String username) {
            this.username = username;
            return this;
        }

        public ReservationDtoBuilder bookId(Long bookId) {
            this.bookId = bookId;
            return this;
        }

        public ReservationDtoBuilder bookTitle(String bookTitle) {
            this.bookTitle = bookTitle;
            return this;
        }

        public ReservationDtoBuilder isbn(String isbn) {
            this.isbn = isbn;
            return this;
        }

        public ReservationDtoBuilder reservationDate(LocalDateTime reservationDate) {
            this.reservationDate = reservationDate;
            return this;
        }

        public ReservationDtoBuilder expiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public ReservationDtoBuilder queuePosition(Integer queuePosition) {
            this.queuePosition = queuePosition;
            return this;
        }

        public ReservationDtoBuilder status(String status) {
            this.status = status;
            return this;
        }

        public ReservationDto build() {
            return new ReservationDto(id, userId, username, bookId, bookTitle, isbn, reservationDate, expiryDate, queuePosition, status);
        }
    }
}
