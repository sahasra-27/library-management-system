package com.smartlibrary.controller;

import com.smartlibrary.entity.User;
import com.smartlibrary.dto.UserDto;
import com.smartlibrary.dto.SignupRequest;
import com.smartlibrary.mapper.LibraryMapper;
import com.smartlibrary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<User> userPage = userService.getAllUsers(query, pageable);
        return ResponseEntity.ok(userPage.map(LibraryMapper::toUserDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(LibraryMapper.toUserDto(user));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody SignupRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .phone(request.getPhone())
                .occupation(request.getOccupation())
                .build();
        User saved = userService.createUser(user, request.getRole());
        return ResponseEntity.ok(LibraryMapper.toUserDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody SignupRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .occupation(request.getOccupation())
                .build();
        User updated = userService.updateUser(id, user, request.getRole());
        return ResponseEntity.ok(LibraryMapper.toUserDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().body("User deleted successfully");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDto> setStatus(
            @PathVariable Long id,
            @RequestParam("status") String status) {
        User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
        User updated = userService.setStatus(id, userStatus);
        return ResponseEntity.ok(LibraryMapper.toUserDto(updated));
    }

    @PostMapping("/{id}/profile-photo")
    public ResponseEntity<UserDto> uploadProfilePhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String photoPath = userService.saveProfilePhoto(id, file);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(LibraryMapper.toUserDto(user));
    }

    @PatchMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword) {
        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().body("Password changed successfully");
    }
}
