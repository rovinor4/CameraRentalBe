package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.MaintenanceRequest;
import com.rvinproject.camerarentalbe.app.enumModel.ItemStatus;
import com.rvinproject.camerarentalbe.app.enumModel.MaintenanceStatus;
import com.rvinproject.camerarentalbe.app.model.Admin;
import com.rvinproject.camerarentalbe.app.model.Item;
import com.rvinproject.camerarentalbe.app.model.ItemMaintenance;
import com.rvinproject.camerarentalbe.app.repository.ItemMaintenanceRepository;
import com.rvinproject.camerarentalbe.app.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ItemMaintenanceService {
    private final ItemMaintenanceRepository itemMaintenanceRepository;
    private final ItemService itemService;

    public Page<ItemMaintenance> maintenances(Specification<ItemMaintenance> specification, Pageable pageable) {
        return itemMaintenanceRepository.findAll(specification, pageable);
    }

    public ItemMaintenance maintenance(Long id) {
        return itemMaintenanceRepository.findById(id).orElseThrow(() -> notFound("maintenance"));
    }

    @Transactional
    public ItemMaintenance saveMaintenance(Long id, MaintenanceRequest request, Admin admin) {
        ItemMaintenance maintenance = id == null ? new ItemMaintenance() : maintenance(id);
        Item item = itemService.item(request.getItemId());
        MaintenanceStatus status = ValidationUtil.enumValue(request.getStatus(), MaintenanceStatus.class, "status");
        maintenance.setItem(item);
        maintenance.setAdmin(admin);
        maintenance.setTitle(request.getTitle());
        maintenance.setDescription(request.getDescription());
        maintenance.setMaintenanceDate(request.getMaintenanceDate());
        maintenance.setCost(request.getCost());
        maintenance.setStatus(status);
        item.setStatus(MaintenanceStatus.in_progress.equals(status)
                ? ItemStatus.maintenance
                : (item.getStock() > 0 ? ItemStatus.available : ItemStatus.inactive));
        return itemMaintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(Long id) {
        itemMaintenanceRepository.delete(maintenance(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
