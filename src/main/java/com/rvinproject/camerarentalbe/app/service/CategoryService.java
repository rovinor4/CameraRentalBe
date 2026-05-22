package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.CategoryRequest;
import com.rvinproject.camerarentalbe.app.model.Category;
import com.rvinproject.camerarentalbe.app.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Page<Category> categories(Specification<Category> specification, Pageable pageable) {
        return categoryRepository.findAll(specification, pageable);
    }

    public Category category(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> notFound("category"));
    }

    public Category saveCategory(Long id, CategoryRequest request) {
        Category category = id == null ? new Category() : category(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.delete(category(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
