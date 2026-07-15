package com.smartlibrary.repository;

import com.smartlibrary.entity.BookImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookImageRepository extends JpaRepository<BookImage, Long> {
    List<BookImage> findByBookId(Long bookId);
    void deleteByBookId(Long bookId);
}
