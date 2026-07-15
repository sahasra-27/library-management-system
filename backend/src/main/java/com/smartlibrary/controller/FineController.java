package com.smartlibrary.controller;

import com.smartlibrary.entity.Fine;
import com.smartlibrary.dto.FineDto;
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
@RequestMapping("/api/fines")
public class FineController {

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
    public ResponseEntity<Page<FineDto>> getFines(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "status", defaultValue = "ALL") String status,
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
            userId = authUserId; // enforce user only sees their own fines
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Fine> finePage = issueService.searchFines(userId, status, query, pageable);
        return ResponseEntity.ok(finePage.map(LibraryMapper::toFineDto));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payFine(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long authUserId = getAuthenticatedUserId(authHeader);
        String authRole = getAuthenticatedUserRole(authHeader);
        if (authUserId == null) {
            throw new com.smartlibrary.exception.LibraryException("Unauthorized", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        // Enforce that student can only pay their own fines
        Fine fine = issueService.searchFines(authUserId, "ALL", null, PageRequest.of(0, 1000)).getContent().stream()
                .filter(f -> f.getId().equals(id))
                .findFirst()
                .orElse(null);
        if ("USER".equalsIgnoreCase(authRole) && fine == null) {
            throw new com.smartlibrary.exception.LibraryException("You cannot pay a fine that does not belong to you.", org.springframework.http.HttpStatus.FORBIDDEN);
        }

        issueService.payFine(id);
        return ResponseEntity.ok().body("Fine paid successfully");
    }

    @PostMapping("/{id}/unpay")
    public ResponseEntity<?> unpayFine(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String authRole = getAuthenticatedUserRole(authHeader);
        if ("USER".equalsIgnoreCase(authRole)) {
            throw new com.smartlibrary.exception.LibraryException("Users are not authorized to mark fines as unpaid.", org.springframework.http.HttpStatus.FORBIDDEN);
        }
        issueService.unpayFine(id);
        return ResponseEntity.ok().body("Fine marked as unpaid successfully");
    }

    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, java.math.BigDecimal>> getFineStats(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long authUserId = getAuthenticatedUserId(authHeader);
        String authRole = getAuthenticatedUserRole(authHeader);
        
        if (authUserId == null) {
            throw new com.smartlibrary.exception.LibraryException("Unauthorized", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        if ("USER".equalsIgnoreCase(authRole)) {
            userId = authUserId; // enforce user only sees their own stats
        }
        return ResponseEntity.ok(issueService.getFineStats(userId));
    }

    @PostMapping
    public ResponseEntity<FineDto> createFine(@RequestBody java.util.Map<String, Object> payload) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        Long issueId = payload.get("issueId") != null && !payload.get("issueId").toString().isEmpty() 
                ? Long.valueOf(payload.get("issueId").toString()) : null;
        java.math.BigDecimal amount = new java.math.BigDecimal(payload.get("amount").toString());
        String reason = (String) payload.get("reason");
        String status = (String) payload.get("status");
        
        Fine f = Fine.builder()
                .amount(amount)
                .reason(reason)
                .status(status != null ? Fine.FineStatus.valueOf(status.toUpperCase()) : Fine.FineStatus.UNPAID)
                .build();
                
        if (payload.get("dueDate") != null && !payload.get("dueDate").toString().isEmpty()) {
            f.setDueDate(java.time.LocalDateTime.parse(payload.get("dueDate").toString()));
        }
        
        Fine saved = issueService.createFineManual(f, userId, issueId);
        return ResponseEntity.ok(LibraryMapper.toFineDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FineDto> updateFine(@PathVariable Long id, @RequestBody java.util.Map<String, Object> payload) {
        Fine updateDetails = new Fine();
        if (payload.get("amount") != null) {
            updateDetails.setAmount(new java.math.BigDecimal(payload.get("amount").toString()));
        }
        if (payload.get("reason") != null) {
            updateDetails.setReason((String) payload.get("reason"));
        }
        if (payload.get("status") != null) {
            updateDetails.setStatus(Fine.FineStatus.valueOf(payload.get("status").toString().toUpperCase()));
        }
        if (payload.get("dueDate") != null && !payload.get("dueDate").toString().isEmpty()) {
            updateDetails.setDueDate(java.time.LocalDateTime.parse(payload.get("dueDate").toString()));
        }
        
        Fine updated = issueService.updateFine(id, updateDetails);
        return ResponseEntity.ok(LibraryMapper.toFineDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFine(@PathVariable Long id) {
        issueService.deleteFine(id);
        return ResponseEntity.ok().body("Fine deleted successfully");
    }
}
