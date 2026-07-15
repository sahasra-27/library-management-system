package com.smartlibrary.repository;

import com.smartlibrary.entity.Fine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {
    Optional<Fine> findByIssuedBookId(Long issueId);

    @Query("SELECT f FROM Fine f WHERE f.issuedBook.user.id = :userId")
    List<Fine> findByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Fine f WHERE f.issuedBook.user.id = :userId AND f.status = 'UNPAID'")
    List<Fine> findUnpaidByUserId(@Param("userId") Long userId);

    @Query("SELECT f FROM Fine f WHERE " +
           "(:userId IS NULL OR f.user.id = :userId) AND " +
           "(:status IS NULL OR f.status = :status) AND " +
           "(:query IS NULL OR (f.issuedBook IS NOT NULL AND LOWER(f.issuedBook.bookCopy.book.title) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "OR LOWER(f.user.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(CONCAT('', f.user.id)) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Fine> searchFines(@Param("userId") Long userId, @Param("status") Fine.FineStatus status, @Param("query") String query, Pageable pageable);

    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.status = 'PAID'")
    BigDecimal sumTotalPaidFines();

    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.status = 'UNPAID' OR f.status = 'OVERDUE'")
    BigDecimal sumTotalUnpaidFines();

    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.user.id = :userId AND f.status = 'PAID'")
    BigDecimal sumTotalPaidFinesByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(f.amount) FROM Fine f WHERE f.user.id = :userId AND (f.status = 'UNPAID' OR f.status = 'OVERDUE')")
    BigDecimal sumTotalUnpaidFinesByUserId(@Param("userId") Long userId);
}
