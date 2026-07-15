package com.smartlibrary.repository;

import com.smartlibrary.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    java.util.Optional<Author> findByName(String name);

    @Query("SELECT a FROM Author a WHERE :query IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Author> searchAuthors(@Param("query") String query, Pageable pageable);
}
