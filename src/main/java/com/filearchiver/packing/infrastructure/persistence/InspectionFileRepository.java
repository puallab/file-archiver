package com.filearchiver.packing.infrastructure.persistence;

import com.filearchiver.packing.domain.InspectionFile;
import com.filearchiver.packing.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InspectionFileRepository extends JpaRepository<InspectionFile, Long> {
    Optional<InspectionFile> findByProductAndInspectedAt(
            Product product,
            LocalDateTime inspectedAt
    );

    @Modifying(clearAutomatically = true)
    @Query("""
            update InspectionFile i
            set i.sensorId = :sensorId, i.uploaderEquipment = :equipment, i.filePath = :filePath
            where i.product = :product
                and i.inspectedAt = :inspectedTime
                and exists (
                        select 1
                        from Product p
                        where p = i.product
                            and p.packaging is null
                    )
    """)
    int updateIfNotPacked(
            @Param("product") Product product,
            @Param("inspectedTime") LocalDateTime inspectedTime,
            @Param("sensorId") String sensorId,
            @Param("equipment") String equipment,
            @Param("filePath") String filePath
    );

}
