package com.smartlibrary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartlibrary.entity.Book;
import com.smartlibrary.dto.BookDto;
import com.smartlibrary.mapper.LibraryMapper;
import com.smartlibrary.service.BookService;
import com.smartlibrary.service.OpenLibraryService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private OpenLibraryService openLibraryService;

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<java.util.Map<String, Object>> fetchBookByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(openLibraryService.fetchBookByIsbn(isbn));
    }

    @GetMapping
    public ResponseEntity<Page<BookDto>> getBooks(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "title") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Book> booksPage = bookService.searchBooks(query, pageable);
        
        Page<BookDto> dtoPage = booksPage.map(LibraryMapper::toBookDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(LibraryMapper.toBookDto(book));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookDto> createBook(
            @RequestPart("book") String bookJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        BookDto dto = mapper.readValue(bookJson, BookDto.class);
        
        Book book = bookService.createBook(dto, file);
        return ResponseEntity.ok(LibraryMapper.toBookDto(book));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookDto> updateBook(
            @PathVariable Long id,
            @RequestPart("book") String bookJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        BookDto dto = mapper.readValue(bookJson, BookDto.class);

        Book book = bookService.updateBook(id, dto, file);
        return ResponseEntity.ok(LibraryMapper.toBookDto(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Long id) {
        bookService.softDeleteBook(id);
        return ResponseEntity.ok().body("Book deleted successfully (soft delete)");
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<?> restoreBook(@PathVariable Long id) {
        bookService.restoreBook(id);
        return ResponseEntity.ok().body("Book restored successfully");
    }

    @PostMapping("/import/{workId}")
    public ResponseEntity<BookDto> importOpenLibraryBook(@PathVariable String workId) {
        Book book = bookService.importOpenLibraryBook(workId);
        return ResponseEntity.ok(LibraryMapper.toBookDto(book));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadCsv(@RequestParam("file") MultipartFile file) {
        bookService.bulkUploadBooksCsv(file);
        return ResponseEntity.ok().body("CSV data uploaded and processed successfully");
    }

    @GetMapping("/export/csv")
    public void exportCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv");
        bookService.exportBooksToCsv(response.getWriter());
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excelBytes = bookService.exportBooksToExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
