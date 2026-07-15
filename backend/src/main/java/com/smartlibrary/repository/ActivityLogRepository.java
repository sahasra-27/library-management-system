package com.smartlibrary.repository;

import com.smartlibrary.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    @Query("SELECT al FROM ActivityLog al WHERE " +
           "(:query IS NULL OR LOWER(al.action) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(al.details) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(al.user.username) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<ActivityLog> searchLogs(@Param("query") String query, Pageable pageable);

    Page<ActivityLog> findByUserId(Long userId, Pageable pageable);
}
