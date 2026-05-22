package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.CategoryDetailRequest;
import com.rvinproject.camerarentalbe.app.model.CategoryDetail;
import com.rvinproject.camerarentalbe.app.repository.CategoryDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryDetailService {
    private final CategoryDetailRepository categoryDetailRepository;
    private final CategoryService categoryService;

    public Page<CategoryDetail> categoryDetails(Specification<CategoryDetail> specification, Pageable pageable) {
        return categoryDetailRepository.findAll(specification, pageable);
    }

    public CategoryDetail categoryDetail(Long id) {
        return categoryDetailRepository.findById(id).orElseThrow(() -> notFound("category detail"));
    }

    public CategoryDetail saveCategoryDetail(Long id, CategoryDetailRequest request) {
        CategoryDetail detail = id == null ? new CategoryDetail() : categoryDetail(id);
        detail.setCategory(categoryService.category(request.getCategoryId()));
        detail.setName(request.getName());
        detail.setDescription(request.getDescription());
        return categoryDetailRepository.save(detail);
    }

    public void deleteCategoryDetail(Long id) {
        categoryDetailRepository.delete(categoryDetail(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
