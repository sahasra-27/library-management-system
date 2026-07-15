package com.smartlibrary.repository;

import com.smartlibrary.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Boolean existsByName(String name);

    @Query("SELECT c FROM Category c WHERE :query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Category> searchCategories(@Param("query") String query, Pageable pageable);
}
