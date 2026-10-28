package com.filearchiver.packing.application;

import com.filearchiver.packing.domain.Product;
import com.filearchiver.packing.infrastructure.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product registerIfAbsent(String serial) {
        return productRepository.findBySerial(serial)
            .orElseGet(() -> productRepository.save(Product.create(serial)));
    }
}
