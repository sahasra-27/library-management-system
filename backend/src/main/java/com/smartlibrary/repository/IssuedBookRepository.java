package com.smartlibrary.repository;

import com.smartlibrary.entity.IssuedBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssuedBookRepository extends JpaRepository<IssuedBook, Long> {
    List<IssuedBook> findByUserId(Long userId);
    List<IssuedBook> findByUserIdAndStatus(Long userId, IssuedBook.IssueStatus status);

    @Query("SELECT ib FROM IssuedBook ib WHERE ib.status = 'ISSUED' OR ib.status = 'OVERDUE'")
    List<IssuedBook> findAllActiveIssues();

    @Query("SELECT ib FROM IssuedBook ib WHERE ib.status = 'OVERDUE'")
    List<IssuedBook> findAllOverdueIssues();

    @Query("SELECT ib FROM IssuedBook ib WHERE ib.dueDate < :now AND ib.status = 'ISSUED'")
    List<IssuedBook> findOverdueIssues(@Param("now") LocalDateTime now);

    @Query("SELECT ib FROM IssuedBook ib WHERE " +
           "(:userId IS NULL OR ib.user.id = :userId) AND " +
           "(:query IS NULL OR LOWER(ib.bookCopy.book.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(ib.user.username) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(ib.bookCopy.barcode) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<IssuedBook> searchIssues(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);

    long countByStatus(IssuedBook.IssueStatus status);

    @Query("SELECT COUNT(ib) FROM IssuedBook ib WHERE ib.issueDate >= :startOfDay")
    long countIssuedToday(@Param("startOfDay") LocalDateTime startOfDay);

    @Query("SELECT COUNT(ib) > 0 FROM IssuedBook ib WHERE ib.user.id = :userId AND ib.bookCopy.book.id = :bookId AND ib.status IN ('ISSUED', 'OVERDUE')")
    boolean existsActiveIssueForUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
}
