package com.smartlibrary.service;

import com.smartlibrary.entity.Notification;
import com.smartlibrary.entity.User;
import com.smartlibrary.repository.NotificationRepository;
import com.smartlibrary.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendNotification(User user, String type, String message) {
        // 1. Save in-app notification
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .message(message)
                .readStatus(false)
                .build();
        notificationRepository.save(notification);

        // 2. Try sending email (with fallback to log output to prevent crashes)
        try {
            if (mailSender != null && user.getEmail() != null && !user.getEmail().contains("your-email")) {
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setTo(user.getEmail());
                mailMessage.setSubject("Smart Library: " + type.replace("_", " "));
                mailMessage.setText(message);
                mailSender.send(mailMessage);
                logger.info("Sent email to {} for event {}", user.getEmail(), type);
            } else {
                logger.info("[MOCK EMAIL] To: {} | Subject: {} | Message: {}", user.getEmail(), type, message);
            }
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}. Logged notification locally.", user.getEmail(), e.getMessage());
        }
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderBySentAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadStatus(userId, false);
    }

    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setReadStatus(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdOrderBySentAtDesc(userId);
        unread.forEach(n -> {
            if (!n.isReadStatus()) {
                n.setReadStatus(true);
                notificationRepository.save(n);
            }
        });
    }
}
