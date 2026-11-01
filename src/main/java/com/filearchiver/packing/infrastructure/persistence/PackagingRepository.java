package com.filearchiver.packing.infrastructure.persistence;

import com.filearchiver.packing.domain.Packaging;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackagingRepository extends JpaRepository<Packaging, Long> {
}
