package com.smartlibrary.controller;

import com.smartlibrary.dto.DashboardStatsDto;
import com.smartlibrary.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ReportService reportService;

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
    public ResponseEntity<DashboardStatsDto> getDashboardStats(
            @RequestParam(value = "studentId", required = false) Long studentId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Long authUserId = getAuthenticatedUserId(authHeader);
        String authRole = getAuthenticatedUserRole(authHeader);
        
        if (authUserId == null) {
            throw new com.smartlibrary.exception.LibraryException("Unauthorized", org.springframework.http.HttpStatus.UNAUTHORIZED);
        }
        if ("USER".equalsIgnoreCase(authRole)) {
            studentId = authUserId; // enforce user only sees their own stats
        }
        
        DashboardStatsDto stats = reportService.getDashboardStats(studentId);
        return ResponseEntity.ok(stats);
    }
}
