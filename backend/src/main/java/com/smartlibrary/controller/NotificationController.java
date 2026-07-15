package com.smartlibrary.controller;

import com.smartlibrary.entity.Notification;
import com.smartlibrary.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam("userId") Long userId) {
        List<Notification> list = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<Long> getUnreadCount(@RequestParam("userId") Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().body("Notification marked as read");
    }

    @PostMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(@RequestParam("userId") Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().body("All notifications marked as read");
    }
}
