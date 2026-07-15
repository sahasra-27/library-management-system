package com.smartlibrary.controller;

import com.smartlibrary.entity.Reservation;
import com.smartlibrary.dto.ReservationDto;
import com.smartlibrary.mapper.LibraryMapper;
import com.smartlibrary.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private IssueService issueService;

    private Long getAuthenticatedUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer mock-token-")) {
            return null;
        }
        try {
            String[] parts = authHeader.replace("Bearer mock-token-", "").split("-");
            return Long.valueOf(parts[0]);
        } catch (Exception e) {
            return null;
        }
    }

    private String getAuthenticatedUserRole(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer mock-token-")) {
            return null;
        }
        try {
            String[] parts = authHeader.replace("Bearer mock-token-", "").split("-");
            return parts[1];
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping
    public ResponseEntity<Page<ReservationDto>> getReservations(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long authUserId = getAuthenticatedUserId(authHeader);
        String authRole = getAuthenticatedUserRole(authHeader);
        
        if (authUserId == null) {
            throw new com.smartlibrary.exception.LibraryException("Unauthorized", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        if ("USER".equalsIgnoreCase(authRole)) {
            userId = authUserId; // enforce user only sees their own reservations
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("reservationDate").descending());
        Page<Reservation> reservations = issueService.searchReservations(userId, status, query, pageable);
        return ResponseEntity.ok(reservations.map(r -> LibraryMapper.toReservationDto(r, issueService.getQueuePosition(r))));
    }

    @PostMapping
    public ResponseEntity<ReservationDto> createReservation(
            @RequestBody java.util.Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long authUserId = getAuthenticatedUserId(authHeader);
        String authRole = getAuthenticatedUserRole(authHeader);
        
        if (authUserId == null) {
            throw new com.smartlibrary.exception.LibraryException("Unauthorized", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        
        Long userId = Long.valueOf(payload.get("userId").toString());
        if ("USER".equalsIgnoreCase(authRole)) {
            userId = authUserId; // override to enforce self-reservation
        }
        
        Long bookId = Long.valueOf(payload.get("bookId").toString());
        String status = (String) payload.get("status");
        
        com.smartlibrary.entity.User user = new com.smartlibrary.entity.User();
        user.setId(userId);
        
        com.smartlibrary.entity.Book book = new com.smartlibrary.entity.Book();
        book.setId(bookId);
        
        Reservation r = Reservation.builder()
                .user(user)
                .book(book)
                .build();
                
        if (payload.get("reservationDate") != null && !payload.get("reservationDate").toString().isEmpty()) {
            r.setReservationDate(java.time.LocalDateTime.parse(payload.get("reservationDate").toString()));
        }
        if (payload.get("expiryDate") != null && !payload.get("expiryDate").toString().isEmpty()) {
            r.setExpiryDate(java.time.LocalDateTime.parse(payload.get("expiryDate").toString()));
        }
        
        Reservation saved = issueService.createReservationAdmin(r, status);
        return ResponseEntity.ok(LibraryMapper.toReservationDto(saved, issueService.getQueuePosition(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDto> updateReservation(@PathVariable Long id, @RequestBody java.util.Map<String, Object> payload) {
        Reservation updateDetails = new Reservation();
        if (payload.get("status") != null) {
            updateDetails.setStatus(Reservation.ReservationStatus.valueOf(payload.get("status").toString().toUpperCase()));
        }
        if (payload.get("expiryDate") != null && !payload.get("expiryDate").toString().isEmpty()) {
            updateDetails.setExpiryDate(java.time.LocalDateTime.parse(payload.get("expiryDate").toString()));
        }
        if (payload.get("reservationDate") != null && !payload.get("reservationDate").toString().isEmpty()) {
            updateDetails.setReservationDate(java.time.LocalDateTime.parse(payload.get("reservationDate").toString()));
        }
        
        Reservation updated = issueService.updateReservation(id, updateDetails);
        return ResponseEntity.ok(LibraryMapper.toReservationDto(updated, issueService.getQueuePosition(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        issueService.deleteReservation(id);
        return ResponseEntity.ok().body("Reservation deleted successfully");
    }
}
