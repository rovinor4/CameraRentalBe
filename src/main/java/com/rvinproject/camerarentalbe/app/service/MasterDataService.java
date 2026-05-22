package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.dto.ApiRequest;
import com.rvinproject.camerarentalbe.app.model.*;
import com.rvinproject.camerarentalbe.app.repository.*;
import com.rvinproject.camerarentalbe.app.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MasterDataService {
    private static final Set<String> IDENTITY_TYPES = Set.of("id_card", "driver_license");
    private static final Set<String> ITEM_STATUS = Set.of("available", "rented", "maintenance", "inactive");
    private static final Set<String> PAYMENT_TYPES = Set.of("qr_code", "bank_transfer", "cash", "e_wallet");
    private static final Set<String> CONTENT_TYPES = Set.of("image", "text");

    private final CustomerRepository customerRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryDetailRepository categoryDetailRepository;
    private final ItemRepository itemRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public Page<Customer> customers(Specification<Customer> specification, Pageable pageable) {
        return customerRepository.findAll(specification, pageable);
    }

    public Customer customer(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> notFound("customer"));
    }

    public Customer saveCustomer(Long id, ApiRequest.CustomerRequest request) {
        ValidationUtil.oneOf(request.getIdentityType(), IDENTITY_TYPES, "identityType");
        Customer customer = id == null ? new Customer() : customer(id);
        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setIdentityType(request.getIdentityType());
        customer.setIdentityNumber(request.getIdentityNumber());
        customer.setIdentityImage(request.getIdentityImage());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.delete(customer(id));
    }

    public Page<Category> categories(Specification<Category> specification, Pageable pageable) {
        return categoryRepository.findAll(specification, pageable);
    }

    public Category category(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> notFound("category"));
    }

    public Category saveCategory(Long id, ApiRequest.CategoryRequest request) {
        Category category = id == null ? new Category() : category(id);
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.delete(category(id));
    }

    public Page<CategoryDetail> categoryDetails(Specification<CategoryDetail> specification, Pageable pageable) {
        return categoryDetailRepository.findAll(specification, pageable);
    }

    public CategoryDetail categoryDetail(Long id) {
        return categoryDetailRepository.findById(id).orElseThrow(() -> notFound("category detail"));
    }

    public CategoryDetail saveCategoryDetail(Long id, ApiRequest.CategoryDetailRequest request) {
        CategoryDetail detail = id == null ? new CategoryDetail() : categoryDetail(id);
        detail.setCategory(category(request.getCategoryId()));
        detail.setName(request.getName());
        detail.setDescription(request.getDescription());
        return categoryDetailRepository.save(detail);
    }

    public void deleteCategoryDetail(Long id) {
        categoryDetailRepository.delete(categoryDetail(id));
    }

    public Page<Item> items(Specification<Item> specification, Pageable pageable) {
        return itemRepository.findAll(specification, pageable);
    }

    public Item item(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> notFound("item"));
    }

    public Item saveItem(Long id, ApiRequest.ItemRequest request) {
        ValidationUtil.oneOf(request.getStatus(), ITEM_STATUS, "status");
        Item item = id == null ? new Item() : item(id);
        item.setCategory(category(request.getCategoryId()));
        item.setCategoryDetail(request.getCategoryDetailId() == null ? null : categoryDetail(request.getCategoryDetailId()));
        item.setName(request.getName());
        item.setBrand(request.getBrand());
        item.setModel(request.getModel());
        item.setSerialNumber(request.getSerialNumber());
        item.setDescription(request.getDescription());
        item.setDailyPrice(request.getDailyPrice());
        item.setStock(request.getStock());
        item.setStatus(request.getStatus());
        item.setImage(request.getImage());
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.delete(item(id));
    }

    public Page<PaymentMethod> paymentMethods(boolean activeOnly, Specification<PaymentMethod> specification, Pageable pageable) {
        if (activeOnly) {
            Specification<PaymentMethod> activeSpec = (root, query, builder) -> builder.isTrue(root.get("active"));
            return paymentMethodRepository.findAll(specification.and(activeSpec), pageable);
        }
        return paymentMethodRepository.findAll(specification, pageable);
    }

    public PaymentMethod paymentMethod(Long id) {
        return paymentMethodRepository.findById(id).orElseThrow(() -> notFound("payment method"));
    }

    public PaymentMethod activePaymentMethod(Long id) {
        PaymentMethod paymentMethod = paymentMethod(id);
        if (!Boolean.TRUE.equals(paymentMethod.getActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "payment method tidak aktif");
        }
        return paymentMethod;
    }

    public PaymentMethod savePaymentMethod(Long id, ApiRequest.PaymentMethodRequest request) {
        ValidationUtil.oneOf(request.getType(), PAYMENT_TYPES, "type");
        ValidationUtil.oneOf(request.getContentType(), CONTENT_TYPES, "contentType");
        PaymentMethod paymentMethod = id == null ? new PaymentMethod() : paymentMethod(id);
        paymentMethod.setName(request.getName());
        paymentMethod.setType(request.getType());
        paymentMethod.setContentType(request.getContentType());
        paymentMethod.setContentValue(request.getContentValue());
        paymentMethod.setActive(request.getActive());
        return paymentMethodRepository.save(paymentMethod);
    }

    public void deletePaymentMethod(Long id) {
        paymentMethodRepository.delete(paymentMethod(id));
    }

    private ResponseStatusException notFound(String label) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, label + " tidak ditemukan");
    }
}
