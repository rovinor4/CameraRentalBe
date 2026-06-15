package com.rvinproject.camerarentalbe.app.service;

import com.rvinproject.camerarentalbe.app.repository.CustomerRepository;
import com.rvinproject.camerarentalbe.app.repository.ItemRepository;
import com.rvinproject.camerarentalbe.app.repository.RentalPaymentRepository;
import com.rvinproject.camerarentalbe.app.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final ItemRepository itemRepository;
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final RentalPaymentRepository rentalPaymentRepository;

    public Map<String, Object> summary() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total_barang", itemRepository.count());
        data.put("total_rental", rentalRepository.count());
        data.put("total_pelanggan", customerRepository.count());
        data.put("total_pendapatan", rentalPaymentRepository.sumPaidAmount());
        return data;
    }
}
