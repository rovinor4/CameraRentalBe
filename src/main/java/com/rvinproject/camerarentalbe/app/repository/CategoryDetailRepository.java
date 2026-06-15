package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.CategoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CategoryDetailRepository extends JpaRepository<CategoryDetail, Long>, JpaSpecificationExecutor<CategoryDetail> {
    List<CategoryDetail> findByCategoryId(Long categoryId);

    Optional<CategoryDetail> findByIdAndCategoryId(Long id, Long categoryId);
}
