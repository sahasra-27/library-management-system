package com.smartlibrary.controller;

import com.smartlibrary.entity.IssuedBook;
import com.smartlibrary.entity.Reservation;
import com.smartlibrary.dto.IssueRequest;
import com.smartlibrary.dto.IssueResponse;
import com.smartlibrary.dto.ReservationDto;
import com.smartlibrary.mapper.LibraryMapper;
import com.smartlibrary.service.IssueService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/issues")
public class IssueController {

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
    public ResponseEntity<Page<IssueResponse>> getIssues(
            @RequestParam(value = "userId", required = false) Long userId,
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
            userId = authUserId; // enforce user only sees their own borrow checkouts
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("issueDate").descending());
        Page<IssuedBook> issuesPage = issueService.searchIssues(userId, query, pageable);
        return ResponseEntity.ok(issuesPage.map(ib -> LibraryMapper.toIssueResponse(ib, null, null)));
    }

    @PostMapping
    public ResponseEntity<IssueResponse> issueBook(@Valid @RequestBody IssueRequest request) {
        IssuedBook issue = issueService.issueBook(request);
        return ResponseEntity.ok(LibraryMapper.toIssueResponse(issue, null, null));
    }

    @PostMapping("/{id}/renew")
    public ResponseEntity<IssueResponse> renewBook(@PathVariable Long id) {
        IssuedBook issue = issueService.renewBook(id);
        return ResponseEntity.ok(LibraryMapper.toIssueResponse(issue, null, null));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ReservationDto> reserveBook(
            @RequestParam("userId") Long userId,
            @RequestParam("bookId") Long bookId) {
        Reservation reservation = issueService.reserveBook(userId, bookId);
        return ResponseEntity.ok(LibraryMapper.toReservationDto(reservation));
    }

    @DeleteMapping("/reserve/{id}")
    public ResponseEntity<?> cancelReservation(
            @PathVariable Long id,
            @RequestParam("userId") Long userId) {
        issueService.cancelReservation(id, userId);
        return ResponseEntity.ok().body("Reservation cancelled successfully");
    }
}
