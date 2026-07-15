package com.smartlibrary.repository;

import com.smartlibrary.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByBookIdAndStatus(Long bookId, Reservation.ReservationStatus status);
    List<Reservation> findByBookIdAndStatusInOrderByReservationDateAsc(Long bookId, List<Reservation.ReservationStatus> statuses);
    boolean existsByUserIdAndBookIdAndStatusIn(Long userId, Long bookId, List<Reservation.ReservationStatus> statuses);

    @Query("SELECT r FROM Reservation r WHERE " +
           "(:userId IS NULL OR r.user.id = :userId) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:query IS NULL OR LOWER(r.book.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.book.isbn) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(r.user.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(CONCAT('', r.user.id)) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Reservation> searchReservations(@Param("userId") Long userId, @Param("status") Reservation.ReservationStatus status, @Param("query") String query, Pageable pageable);

    long countByStatus(Reservation.ReservationStatus status);
}
