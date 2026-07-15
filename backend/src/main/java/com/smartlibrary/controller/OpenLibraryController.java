package com.smartlibrary.controller;

import com.smartlibrary.dto.*;
import com.smartlibrary.service.OpenLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/openlibrary")
public class OpenLibraryController {

    @Autowired
    private OpenLibraryService openLibraryService;

    @GetMapping("/search")
    public ResponseEntity<List<OpenLibraryBookDto>> searchBooks(
            @RequestParam("q") String query,
            @RequestParam(value = "type", defaultValue = "all") String type) {
        return ResponseEntity.ok(openLibraryService.searchBooks(query, type));
    }

    @GetMapping("/works/{workId}")
    public ResponseEntity<OpenLibraryWorkDetailDto> getWorkDetail(@PathVariable String workId) {
        return ResponseEntity.ok(openLibraryService.getWorkDetail(workId));
    }

    @GetMapping("/authors/{authorId}")
    public ResponseEntity<OpenLibraryAuthorDto> getAuthorDetail(@PathVariable String authorId) {
        return ResponseEntity.ok(openLibraryService.getAuthorDetail(authorId));
    }

    @GetMapping("/subjects/{subjectName}")
    public ResponseEntity<List<OpenLibraryBookDto>> getBooksBySubject(@PathVariable String subjectName) {
        return ResponseEntity.ok(openLibraryService.getBooksBySubject(subjectName));
    }

    @GetMapping("/works/{workId}/editions")
    public ResponseEntity<List<OpenLibraryEditionDto>> getEditions(@PathVariable String workId) {
        return ResponseEntity.ok(openLibraryService.getEditions(workId));
    }
}
