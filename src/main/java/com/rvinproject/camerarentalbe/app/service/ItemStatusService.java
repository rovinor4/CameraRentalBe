package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.ItemStatusRequest;
import com.rvinproject.camerarentalbe.app.enumModel.ItemStatus;
import com.rvinproject.camerarentalbe.app.model.Item;
import com.rvinproject.camerarentalbe.app.model.ItemStatusRecord;
import com.rvinproject.camerarentalbe.app.repository.ItemStatusRepository;
import com.rvinproject.camerarentalbe.app.repository.ItemRepository;
import com.rvinproject.camerarentalbe.app.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemStatusService {
    private final ItemStatusRepository itemStatusRepository;
    private final ItemRepository itemRepository;

    public Page<ItemStatusRecord> itemStatuses(Specification<ItemStatusRecord> specification, Pageable pageable) {
        return itemStatusRepository.findAll(specification, pageable);
    }

    public ItemStatusRecord itemStatus(Long id) {
        return itemStatusRepository.findById(id).orElseThrow(() -> notFound("item status"));
    }

    public ItemStatusRecord availableItemStatus(Long itemId) {
        return itemStatusRepository.findFirstByItemIdAndStatusOrderByIdAsc(itemId, ItemStatus.available)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item status available tidak ditemukan"));
    }

    public List<ItemStatusRecord> availableItemStatuses(Long itemId, int quantity) {
        List<ItemStatusRecord> statuses = itemStatusRepository.findByItemIdAndStatus(itemId, ItemStatus.available);
        if (statuses.size() < quantity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stok item tidak cukup");
        }
        return statuses.subList(0, quantity);
    }

    @Transactional
    public ItemStatusRecord updateItemStatus(Long id, ItemStatusRequest request) {
        ItemStatusRecord itemStatus = itemStatus(id);
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status wajib diisi");
        }
        itemStatus.setStatus(ValidationUtil.enumValue(request.getStatus(), ItemStatus.class, "status"));
        syncItemStock(itemStatus.getItem());
        return itemStatus;
    }

    @Transactional
    public ItemStatusRecord createItemStatus(Long itemId, ItemStatusRequest request) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> notFound("item"));
        ItemStatusRecord itemStatus = new ItemStatusRecord();
        itemStatus.setItem(item);
        itemStatus.setStatus(request.getStatus() == null || request.getStatus().isBlank()
                ? ItemStatus.available
                : ValidationUtil.enumValue(request.getStatus(), ItemStatus.class, "status"));
        ItemStatusRecord savedItemStatus = itemStatusRepository.save(itemStatus);
        itemStatusRepository.flush();
        syncItemStock(item);
        return savedItemStatus;
    }

    @Transactional
    public void deleteItemStatus(Long id) {
        ItemStatusRecord itemStatus = itemStatus(id);
        Item item = itemStatus.getItem();
        itemStatusRepository.delete(itemStatus);
        itemStatusRepository.flush();
        syncItemStock(item);
    }

    public ItemStatusRecord createAvailable(Item item) {
        ItemStatusRecord itemStatus = new ItemStatusRecord();
        itemStatus.setItem(item);
        itemStatus.setStatus(ItemStatus.available);
        return itemStatusRepository.save(itemStatus);
    }

    public void syncItemStock(Item item) {
        item.setStock((int) itemStatusRepository.countByItemId(item.getId()));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
