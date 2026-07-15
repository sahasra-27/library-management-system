package com.smartlibrary.controller;

import com.smartlibrary.entity.User;
import com.smartlibrary.dto.*;
import com.smartlibrary.service.ActivityLogService;
import com.smartlibrary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityLogService auditLog;

    @PostMapping("/login")
    public ResponseEntity<UserSessionDto> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        
        return ResponseEntity.ok(UserSessionDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .occupation(user.getOccupation())
                .token("mock-token-" + user.getId() + "-" + user.getRole().name())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(signUpRequest.getPassword())
                .phone(signUpRequest.getPhone())
                .occupation(signUpRequest.getOccupation())
                .build();

        User saved = userService.createUser(user, signUpRequest.getRole());
        return ResponseEntity.ok(new MessageResponse("User registered successfully with ID: " + saved.getId()));
    }

    @PostMapping("/forgotpassword")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        userService.initiateForgotPassword(request.getEmail());
        return ResponseEntity.ok(new MessageResponse("Password reset instructions sent to your email."));
    }

    @PostMapping("/resetpassword")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPasswordWithToken(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(new MessageResponse("Password reset successfully."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(new MessageResponse("Logout successful."));
    }

    public static class MessageResponse {
        private String message;
        public MessageResponse(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
