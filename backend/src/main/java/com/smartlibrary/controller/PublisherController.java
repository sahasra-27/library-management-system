package com.smartlibrary.controller;

import com.smartlibrary.entity.Publisher;
import com.smartlibrary.dto.PublisherDto;
import com.smartlibrary.mapper.LibraryMapper;
import com.smartlibrary.service.PublisherService;
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
@RequestMapping("/api/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping
    public ResponseEntity<Page<PublisherDto>> getPublishers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Publisher> publisherPage = publisherService.searchPublishers(query, pageable);
        return ResponseEntity.ok(publisherPage.map(LibraryMapper::toPublisherDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PublisherDto>> getAllPublishers() {
        List<Publisher> list = publisherService.getAllPublishers();
        return ResponseEntity.ok(list.stream().map(LibraryMapper::toPublisherDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublisherDto> getPublisherById(@PathVariable Long id) {
        Publisher publisher = publisherService.getPublisherById(id);
        return ResponseEntity.ok(LibraryMapper.toPublisherDto(publisher));
    }

    @PostMapping
    public ResponseEntity<PublisherDto> createPublisher(@Valid @RequestBody PublisherDto dto) {
        Publisher publisher = publisherService.createPublisher(dto);
        return ResponseEntity.ok(LibraryMapper.toPublisherDto(publisher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PublisherDto> updatePublisher(@PathVariable Long id, @Valid @RequestBody PublisherDto dto) {
        Publisher publisher = publisherService.updatePublisher(id, dto);
        return ResponseEntity.ok(LibraryMapper.toPublisherDto(publisher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePublisher(@PathVariable Long id) {
        publisherService.deletePublisher(id);
        return ResponseEntity.ok().body("Publisher deleted successfully");
    }
}
