package com.smartlibrary.repository;

import com.smartlibrary.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    Optional<Book> findByIsbn(String isbn);
    Boolean existsByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.status = 'ACTIVE'")
    Page<Book> findAllActive(Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = 'ACTIVE' AND (" +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.author.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.publisher.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.category.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Book> searchBooksActive(@Param("query") String query, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.status = 'ACTIVE' AND b.category.id = :categoryId")
    List<Book> findByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(b) FROM Book b WHERE b.status = 'ACTIVE'")
    long countActiveBooks();

    @Query("SELECT SUM(b.availableQuantity) FROM Book b WHERE b.status = 'ACTIVE'")
    Long countAvailableBooks();
}
