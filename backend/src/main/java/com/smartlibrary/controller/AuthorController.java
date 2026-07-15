package com.smartlibrary.controller;

import com.smartlibrary.entity.Author;
import com.smartlibrary.dto.AuthorDto;
import com.smartlibrary.mapper.LibraryMapper;
import com.smartlibrary.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public ResponseEntity<Page<AuthorDto>> getAuthors(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Author> authorPage = authorService.searchAuthors(query, pageable);
        return ResponseEntity.ok(authorPage.map(LibraryMapper::toAuthorDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AuthorDto>> getAllAuthors() {
        List<Author> list = authorService.getAllAuthors();
        return ResponseEntity.ok(list.stream().map(LibraryMapper::toAuthorDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> getAuthorById(@PathVariable Long id) {
        Author author = authorService.getAuthorById(id);
        return ResponseEntity.ok(LibraryMapper.toAuthorDto(author));
    }

    @PostMapping
    public ResponseEntity<AuthorDto> createAuthor(@Valid @RequestBody AuthorDto dto) {
        Author author = authorService.createAuthor(dto);
        return ResponseEntity.ok(LibraryMapper.toAuthorDto(author));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDto> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDto dto) {
        Author author = authorService.updateAuthor(id, dto);
        return ResponseEntity.ok(LibraryMapper.toAuthorDto(author));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.ok().body("Author deleted successfully");
    }
}
