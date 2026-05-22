package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.CategoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryDetailRepository extends JpaRepository<CategoryDetail, Long>, JpaSpecificationExecutor<CategoryDetail> {
}
