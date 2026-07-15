package com.smartlibrary.service;

import com.smartlibrary.entity.User;
import com.smartlibrary.exception.LibraryException;
import com.smartlibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogService auditLog;

    @Autowired
    private NotificationService notificationService;

    private final Path rootUploads = Paths.get("uploads");

    public UserService() {
        try {
            if (!Files.exists(rootUploads)) {
                Files.createDirectories(rootUploads);
            }
        } catch (IOException e) {
            // Directory creation failure
        }
    }

    public Page<User> getAllUsers(String query, Pageable pageable) {
        return userRepository.searchUsers(query, pageable);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new LibraryException("User not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    public User createUser(User user, String strRole) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new LibraryException("Username is already taken!", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new LibraryException("Email is already in use!", HttpStatus.BAD_REQUEST);
        }

        user.setPassword(user.getPassword()); // Store password as plain text
        user.setStatus(User.UserStatus.ACTIVE);
        user.setRole(mapRole(strRole));

        User saved = userRepository.save(user);
        auditLog.logActivity(saved, "USER_CREATED", "Created user: " + saved.getUsername());
        return saved;
    }

    public User updateUser(Long id, User updateDetails, String strRole) {
        User user = getUserById(id);

        if (!user.getUsername().equals(updateDetails.getUsername()) && userRepository.existsByUsername(updateDetails.getUsername())) {
            throw new LibraryException("Username is already taken!", HttpStatus.BAD_REQUEST);
        }
        if (!user.getEmail().equals(updateDetails.getEmail()) && userRepository.existsByEmail(updateDetails.getEmail())) {
            throw new LibraryException("Email is already in use!", HttpStatus.BAD_REQUEST);
        }

        user.setUsername(updateDetails.getUsername());
        user.setEmail(updateDetails.getEmail());
        user.setPhone(updateDetails.getPhone());
        user.setOccupation(updateDetails.getOccupation());
        if (strRole != null && !strRole.trim().isEmpty()) {
            user.setRole(mapRole(strRole));
        }

        User saved = userRepository.save(user);
        auditLog.logActivity(saved, "USER_UPDATED", "Updated user profile: " + saved.getUsername());
        return saved;
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        auditLog.logActivity(null, "USER_DELETED", "Deleted user: " + user.getUsername());
    }

    public User setStatus(Long id, User.UserStatus status) {
        User user = getUserById(id);
        user.setStatus(status);
        User saved = userRepository.save(user);
        auditLog.logActivity(saved, "USER_STATUS_CHANGED", "Changed status of " + user.getUsername() + " to " + status);
        notificationService.sendNotification(saved, "ACCOUNT_STATUS", "Your account status has been updated to: " + status);
        return saved;
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (!user.getPassword().equals(oldPassword)) {
            throw new LibraryException("Invalid current password", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        auditLog.logActivity(user, "PASSWORD_CHANGED", "Changed password successfully");
        notificationService.sendNotification(user, "PASSWORD_UPDATE", "Your password was changed successfully.");
    }

    public String saveProfilePhoto(Long userId, MultipartFile file) {
        User user = getUserById(userId);
        try {
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path target = this.rootUploads.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            String photoPath = "/api/public/uploads/" + filename;
            user.setProfilePhoto(photoPath);
            userRepository.save(user);
            auditLog.logActivity(user, "PROFILE_PHOTO_UPDATED", "Updated profile picture");
            return photoPath;
        } catch (IOException e) {
            throw new LibraryException("Could not store image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void initiateForgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new LibraryException("No user found with email " + email, HttpStatus.NOT_FOUND));

        String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(temporaryPassword);
        userRepository.save(user);

        notificationService.sendNotification(user, "PASSWORD_RESET", 
                "You requested a password reset. Your temporary password is: " + temporaryPassword + ". Please login and change it immediately.");
    }

    public User login(String usernameOrEmail, String password) {
        Optional<User> optUser = userRepository.findByUsername(usernameOrEmail);
        if (optUser.isEmpty()) {
            optUser = userRepository.findByEmail(usernameOrEmail);
        }

        if (optUser.isEmpty()) {
            throw new LibraryException("Username or Email not found", HttpStatus.UNAUTHORIZED);
        }

        User user = optUser.get();
        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            throw new LibraryException("User account is suspended.", HttpStatus.FORBIDDEN);
        }

        if (!user.getPassword().equals(password)) {
            throw new LibraryException("Invalid credentials password mismatch", HttpStatus.UNAUTHORIZED);
        }

        auditLog.logActivity(user, "LOGIN", "Logged into system");
        return user;
    }

    public void resetPasswordWithToken(String token, String newPassword) {
        User user = userRepository.findByPassword(token)
                .orElseThrow(() -> new LibraryException("Invalid or expired reset token.", HttpStatus.BAD_REQUEST));
        user.setPassword(newPassword);
        userRepository.save(user);
        auditLog.logActivity(user, "PASSWORD_RESET_TOKEN", "Reset password using temporary token");
    }

    private User.Role mapRole(String strRole) {
        if (strRole == null) return User.Role.USER;
        try {
            return User.Role.valueOf(strRole.toUpperCase().replace("ROLE_", ""));
        } catch (IllegalArgumentException e) {
            return User.Role.USER;
        }
    }
}
