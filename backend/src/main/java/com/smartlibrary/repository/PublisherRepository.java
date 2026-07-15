package com.smartlibrary.repository;

import com.smartlibrary.entity.Publisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {
    java.util.Optional<Publisher> findByName(String name);

    @Query("SELECT p FROM Publisher p WHERE :query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Publisher> searchPublishers(@Param("query") String query, Pageable pageable);
}
