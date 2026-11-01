package com.filearchiver.packing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Packaging {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String packagingNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackagingType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    private String zipKey;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private Packaging(String packagingNo, PackagingType type, Status status){
        this.packagingNo = packagingNo;
        this.type = type;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static Packaging createBox(String packagingNo){
        return Packaging.builder()
                .packagingNo(packagingNo)
                .type(PackagingType.BOX)
                .status(Status.PENDING)
                .build();
    }
}
