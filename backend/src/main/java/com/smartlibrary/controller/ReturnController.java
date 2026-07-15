package com.smartlibrary.controller;

import com.smartlibrary.entity.ReturnedBook;
import com.smartlibrary.entity.User;
import com.smartlibrary.dto.ReturnRequest;
import com.smartlibrary.dto.IssueResponse;
import com.smartlibrary.mapper.LibraryMapper;
import com.smartlibrary.service.IssueService;
import com.smartlibrary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/returns")
public class ReturnController {

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;

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
    public ResponseEntity<Page<IssueResponse>> getReturns(
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
            userId = authUserId; // enforce user only sees their own returns
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("returnDate").descending());
        Page<ReturnedBook> returnedPage = issueService.searchReturns(userId, query, pageable);
        return ResponseEntity.ok(returnedPage.map(rb -> LibraryMapper.toIssueResponse(rb.getIssuedBook(), null, rb)));
    }

    @PostMapping
    public ResponseEntity<IssueResponse> returnBook(
            @Valid @RequestBody ReturnRequest request,
            @RequestParam(value = "processedBy", required = false) Long processedBy,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long authUserId = getAuthenticatedUserId(authHeader);
        String authRole = getAuthenticatedUserRole(authHeader);
        
        if (authUserId == null) {
            throw new com.smartlibrary.exception.LibraryException("Unauthorized", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        if ("USER".equalsIgnoreCase(authRole)) {
            processedBy = authUserId; // user can only return for themselves
        } else if (processedBy == null) {
            processedBy = authUserId; // default processor
        }

        User processor = userService.getUserById(processedBy);
        ReturnedBook returnedBook = issueService.returnBook(request, processor);
        return ResponseEntity.ok(LibraryMapper.toIssueResponse(returnedBook.getIssuedBook(), null, returnedBook));
    }
}
