package com.smartlibrary.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @CreationTimestamp
    @Column(name = "reservation_date", updatable = false)
    private LocalDateTime reservationDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    public enum ReservationStatus {
        PENDING,
        APPROVED,
        READY_FOR_PICKUP,
        CANCELLED,
        COMPLETED
    }

    // Constructors
    public Reservation() {
    }

    public Reservation(Long id, User user, Book book, LocalDateTime reservationDate, LocalDateTime expiryDate, ReservationStatus status) {
        this.id = id;
        this.user = user;
        this.book = book;
        this.reservationDate = reservationDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    // Builder
    public static ReservationBuilder builder() {
        return new ReservationBuilder();
    }

    public static class ReservationBuilder {
        private Long id;
        private User user;
        private Book book;
        private LocalDateTime reservationDate;
        private LocalDateTime expiryDate;
        private ReservationStatus status;

        ReservationBuilder() {
        }

        public ReservationBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ReservationBuilder user(User user) {
            this.user = user;
            return this;
        }

        public ReservationBuilder book(Book book) {
            this.book = book;
            return this;
        }

        public ReservationBuilder reservationDate(LocalDateTime reservationDate) {
            this.reservationDate = reservationDate;
            return this;
        }

        public ReservationBuilder expiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public ReservationBuilder status(ReservationStatus status) {
            this.status = status;
            return this;
        }

        public Reservation build() {
            return new Reservation(id, user, book, reservationDate, expiryDate, status);
        }
    }
}
