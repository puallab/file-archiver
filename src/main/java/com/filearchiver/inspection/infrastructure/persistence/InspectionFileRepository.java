package com.filearchiver.inspection.infrastructure.persistence;

import com.filearchiver.inspection.domain.InspectionFile;
import com.filearchiver.packing.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InspectionFileRepository extends JpaRepository<InspectionFile, Long> {
    Optional<InspectionFile> findByProductAndInspectedAt(Product product, LocalDateTime inspectedAt);
}
