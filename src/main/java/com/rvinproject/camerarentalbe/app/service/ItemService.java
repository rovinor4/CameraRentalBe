package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.request.ItemRequest;
import com.rvinproject.camerarentalbe.app.enumModel.ItemStatus;
import com.rvinproject.camerarentalbe.app.model.Item;
import com.rvinproject.camerarentalbe.app.repository.ItemRepository;
import com.rvinproject.camerarentalbe.app.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final CategoryDetailService categoryDetailService;

    public Page<Item> items(Specification<Item> specification, Pageable pageable) {
        return itemRepository.findAll(specification, pageable);
    }

    public Item item(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> notFound("item"));
    }

    public Item saveItem(Long id, ItemRequest request) {
        Item item = id == null ? new Item() : item(id);
        item.setCategory(categoryService.category(request.getCategoryId()));
        item.setCategoryDetail(request.getCategoryDetailId() == null ? null : categoryDetailService.categoryDetail(request.getCategoryDetailId()));
        item.setName(request.getName());
        item.setBrand(request.getBrand());
        item.setModel(request.getModel());
        item.setSerialNumber(request.getSerialNumber());
        item.setDescription(request.getDescription());
        item.setDailyPrice(request.getDailyPrice());
        item.setStock(request.getStock());
        item.setStatus(ValidationUtil.enumValue(request.getStatus(), ItemStatus.class, "status"));
        item.setImage(request.getImage());
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.delete(item(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
