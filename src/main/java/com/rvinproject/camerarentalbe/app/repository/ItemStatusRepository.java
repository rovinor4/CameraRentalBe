package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.enumModel.ItemStatus;
import com.rvinproject.camerarentalbe.app.model.ItemStatusRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ItemStatusRepository extends JpaRepository<ItemStatusRecord, Long>, JpaSpecificationExecutor<ItemStatusRecord> {
    long countByItemId(Long itemId);

    long countByItemIdAndStatus(Long itemId, ItemStatus status);

    List<ItemStatusRecord> findByItemIdAndStatus(Long itemId, ItemStatus status);

    Optional<ItemStatusRecord> findFirstByItemIdAndStatusOrderByIdAsc(Long itemId, ItemStatus status);

    Page<ItemStatusRecord> findDistinctByStatus(ItemStatus status, Pageable pageable);
}
