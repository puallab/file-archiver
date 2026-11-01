package com.filearchiver.packing.infrastructure.persistence;

import com.filearchiver.packing.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findWithLockBySerial(String serial);

    Optional<List<Product>> findBySerialIn(List<String> serials);
}
