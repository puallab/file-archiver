package com.filearchiver.packing.domain;

import com.filearchiver.global.exception.BusinessException;
import com.filearchiver.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "inspection_file",
    uniqueConstraints = @UniqueConstraint(name = "uk_inspection_file_product_id_inspected_at",
        columnNames = {"product_id", "inspected_at"}))
public class InspectionFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private LocalDateTime inspectedAt;

    private String sensorId;

    private String uploaderEquipment;

    @Column(nullable = false, length = 500)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder(access = AccessLevel.PRIVATE)
    private InspectionFile(
        Product product,
        LocalDateTime inspectedAt,
        String sensorId,
        String uploaderEquipment,
        String filePath
    ) {
        this.product = product;
        this.inspectedAt = inspectedAt;
        this.sensorId = sensorId;
        this.uploaderEquipment = uploaderEquipment;
        this.filePath = filePath;
        this.createdAt = LocalDateTime.now();
    }

    public static InspectionFile create(
        Product product,
        LocalDateTime inspectedAt,
        String sensorId,
        String uploaderEquipment,
        String filePath
    ) {

        if (inspectedAt.isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_INSPECTION_TIME);
        }

        return InspectionFile.builder()
            .product(product)
            .inspectedAt(inspectedAt)
            .sensorId(sensorId)
            .uploaderEquipment(uploaderEquipment)
            .filePath(filePath)
            .build();
    }

    public void updateInfo(String sensorId, String uploaderEquipment, String filePath) {
        this.sensorId = sensorId;
        this.uploaderEquipment = uploaderEquipment;
        this.filePath = filePath;
    }
}
