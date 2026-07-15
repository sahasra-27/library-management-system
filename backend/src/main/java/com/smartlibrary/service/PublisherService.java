package com.smartlibrary.service;

import com.smartlibrary.entity.Publisher;
import com.smartlibrary.dto.PublisherDto;
import com.smartlibrary.exception.LibraryException;
import com.smartlibrary.repository.PublisherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private ActivityLogService auditLog;

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public Page<Publisher> searchPublishers(String query, Pageable pageable) {
        return publisherRepository.searchPublishers(query, pageable);
    }

    public Publisher getPublisherById(Long id) {
        return publisherRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Publisher not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    public Publisher createPublisher(PublisherDto dto) {
        Publisher publisher = Publisher.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .build();
        Publisher saved = publisherRepository.save(publisher);
        auditLog.logActivity(null, "PUBLISHER_ADDED", "Added publisher: " + saved.getName());
        return saved;
    }

    public Publisher updatePublisher(Long id, PublisherDto dto) {
        Publisher publisher = getPublisherById(id);
        publisher.setName(dto.getName());
        publisher.setAddress(dto.getAddress());
        publisher.setPhone(dto.getPhone());
        Publisher saved = publisherRepository.save(publisher);
        auditLog.logActivity(null, "PUBLISHER_UPDATED", "Updated publisher: " + saved.getName());
        return saved;
    }

    public void deletePublisher(Long id) {
        Publisher publisher = getPublisherById(id);
        publisherRepository.delete(publisher);
        auditLog.logActivity(null, "PUBLISHER_DELETED", "Deleted publisher: " + publisher.getName());
    }
}
