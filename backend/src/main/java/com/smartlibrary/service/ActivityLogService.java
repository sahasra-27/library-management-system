package com.smartlibrary.service;

import com.smartlibrary.entity.ActivityLog;
import com.smartlibrary.entity.User;
import com.smartlibrary.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logActivity(User user, String action, String details) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .action(action)
                .details(details)
                .build();
        activityLogRepository.save(log);
    }

    public Page<ActivityLog> getLogs(String query, Pageable pageable) {
        return activityLogRepository.searchLogs(query, pageable);
    }

    public Page<ActivityLog> getUserLogs(Long userId, Pageable pageable) {
        return activityLogRepository.findByUserId(userId, pageable);
    }
}
