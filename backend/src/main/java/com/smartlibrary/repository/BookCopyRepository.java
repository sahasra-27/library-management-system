package com.smartlibrary.repository;

import com.smartlibrary.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    Optional<BookCopy> findByBarcode(String barcode);
    List<BookCopy> findByBookId(Long bookId);

    @Query("SELECT bc FROM BookCopy bc WHERE bc.book.id = :bookId AND bc.status = 'AVAILABLE'")
    List<BookCopy> findAvailableCopiesByBookId(@Param("bookId") Long bookId);

    long countByBookId(Long bookId);
}
