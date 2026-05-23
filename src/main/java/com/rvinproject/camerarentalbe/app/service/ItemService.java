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
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final CategoryService categoryService;
    private final CategoryDetailService categoryDetailService;
    private final FileStorageService fileStorageService;

    public Page<Item> items(Specification<Item> specification, Pageable pageable) {
        return itemRepository.findAll(specification, pageable);
    }

    public Item item(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> notFound("item"));
    }

    public Item saveItem(Long id, ItemRequest request) {
        Item item = id == null ? new Item() : item(id);
        String oldImage = item.getImage();
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
        String uploadedImage = fileStorageService.storeImage(request.getImageUpload(), "public");
        if (uploadedImage == null && id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image_upload wajib diisi");
        }
        if (uploadedImage != null) {
            item.setImage(uploadedImage);
        } else if (!StringUtils.hasText(item.getImage())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image_upload wajib diisi");
        }
        Item savedItem = itemRepository.save(item);
        if (uploadedImage != null) {
            fileStorageService.deleteStoredFile(oldImage);
        }
        return savedItem;
    }

    public void deleteItem(Long id) {
        Item item = item(id);
        itemRepository.delete(item);
        fileStorageService.deleteStoredFile(item.getImage());
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
