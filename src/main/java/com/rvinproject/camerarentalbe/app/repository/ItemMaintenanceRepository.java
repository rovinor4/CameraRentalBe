package com.rvinproject.camerarentalbe.app.repository;

import com.rvinproject.camerarentalbe.app.model.ItemMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ItemMaintenanceRepository extends JpaRepository<ItemMaintenance, Long>, JpaSpecificationExecutor<ItemMaintenance> {
}
