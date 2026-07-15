package com.smartlibrary.controller;

import com.smartlibrary.entity.ActivityLog;
import com.smartlibrary.dto.ActivityLogDto;
import com.smartlibrary.mapper.LibraryMapper;
import com.smartlibrary.service.ActivityLogService;
import com.smartlibrary.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ActivityLogService auditLogService;

    @GetMapping("/pdf/{type}")
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable String type) {
        byte[] pdfBytes;
        String filename;

        switch (type.toLowerCase()) {
            case "books":
                pdfBytes = reportService.generateBooksPdfReport();
                filename = "books_report.pdf";
                break;
            case "users":
                pdfBytes = reportService.generateUsersPdfReport();
                filename = "users_report.pdf";
                break;
            case "issues":
                pdfBytes = reportService.generateIssuesPdfReport();
                filename = "issues_report.pdf";
                break;
            case "fines":
                pdfBytes = reportService.generateFinesPdfReport();
                filename = "fines_report.pdf";
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<ActivityLogDto>> getAuditLogs(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<ActivityLog> logs = auditLogService.getLogs(query, pageable);
        return ResponseEntity.ok(logs.map(LibraryMapper::toActivityLogDto));
    }
}
