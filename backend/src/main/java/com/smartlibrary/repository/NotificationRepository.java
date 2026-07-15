package com.smartlibrary.repository;

import com.smartlibrary.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);
    Page<Notification> findByUserIdOrderBySentAtDesc(Long userId, Pageable pageable);
    long countByUserIdAndReadStatus(Long userId, boolean readStatus);
}
