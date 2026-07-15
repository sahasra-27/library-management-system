package com.smartlibrary.service;

import com.smartlibrary.entity.Author;
import com.smartlibrary.dto.AuthorDto;
import com.smartlibrary.exception.LibraryException;
import com.smartlibrary.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ActivityLogService auditLog;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Page<Author> searchAuthors(String query, Pageable pageable) {
        return authorRepository.searchAuthors(query, pageable);
    }

    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Author not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    public Author createAuthor(AuthorDto dto) {
        Author author = Author.builder()
                .name(dto.getName())
                .biography(dto.getBiography())
                .build();
        Author saved = authorRepository.save(author);
        auditLog.logActivity(null, "AUTHOR_ADDED", "Added author: " + saved.getName());
        return saved;
    }

    public Author updateAuthor(Long id, AuthorDto dto) {
        Author author = getAuthorById(id);
        author.setName(dto.getName());
        author.setBiography(dto.getBiography());
        Author saved = authorRepository.save(author);
        auditLog.logActivity(null, "AUTHOR_UPDATED", "Updated author: " + saved.getName());
        return saved;
    }

    public void deleteAuthor(Long id) {
        Author author = getAuthorById(id);
        authorRepository.delete(author);
        auditLog.logActivity(null, "AUTHOR_DELETED", "Deleted author: " + author.getName());
    }
}
