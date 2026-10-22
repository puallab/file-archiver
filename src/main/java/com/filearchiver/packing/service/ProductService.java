package com.filearchiver.packing.service;

import com.filearchiver.packing.domain.Product;
import com.filearchiver.packing.infrastructure.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product registerIfAbsent(String serial) {
        return productRepository.findBySerial(serial)
            .orElseGet(() -> productRepository.save(Product.create(serial)));
    }
}
