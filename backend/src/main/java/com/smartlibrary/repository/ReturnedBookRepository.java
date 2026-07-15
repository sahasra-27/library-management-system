package com.smartlibrary.repository;

import com.smartlibrary.entity.ReturnedBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReturnedBookRepository extends JpaRepository<ReturnedBook, Long> {
    @Query("SELECT rb FROM ReturnedBook rb WHERE rb.issuedBook.user.id = :userId")
    Page<ReturnedBook> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT rb FROM ReturnedBook rb WHERE " +
           "(:userId IS NULL OR rb.issuedBook.user.id = :userId) AND " +
           "(:query IS NULL OR LOWER(rb.issuedBook.bookCopy.book.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(rb.issuedBook.user.username) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ReturnedBook> searchReturns(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    @Query("SELECT COUNT(rb) FROM ReturnedBook rb WHERE rb.returnDate >= :startOfDay")
    long countReturnedToday(@Param("startOfDay") LocalDateTime startOfDay);
}
