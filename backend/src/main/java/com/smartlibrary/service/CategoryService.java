package com.smartlibrary.service;

import com.smartlibrary.entity.Category;
import com.smartlibrary.dto.CategoryDto;
import com.smartlibrary.exception.LibraryException;
import com.smartlibrary.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ActivityLogService auditLog;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Page<Category> searchCategories(String query, Pageable pageable) {
        return categoryRepository.searchCategories(query, pageable);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new LibraryException("Category not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    public Category createCategory(CategoryDto dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new LibraryException("Category already exists: " + dto.getName(), HttpStatus.BAD_REQUEST);
        }
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        Category saved = categoryRepository.save(category);
        auditLog.logActivity(null, "CATEGORY_ADDED", "Added category: " + saved.getName());
        return saved;
    }

    public Category updateCategory(Long id, CategoryDto dto) {
        Category category = getCategoryById(id);
        if (!category.getName().equals(dto.getName()) && categoryRepository.existsByName(dto.getName())) {
            throw new LibraryException("Category already exists: " + dto.getName(), HttpStatus.BAD_REQUEST);
        }
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        Category saved = categoryRepository.save(category);
        auditLog.logActivity(null, "CATEGORY_UPDATED", "Updated category ID: " + id);
        return saved;
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
        auditLog.logActivity(null, "CATEGORY_DELETED", "Deleted category: " + category.getName());
    }
}
