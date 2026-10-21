package com.filearchiver.packing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String serial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_id")
    private Packaging packaging;

    private Long fileId;

    @Builder(access = AccessLevel.PRIVATE)
    private Product(String serial, Packaging packaging, Long fileId) {
        this.serial = serial;
        this.packaging = packaging;
        this.fileId = fileId;
    }

    public static Product create(String serial) {
        return Product.builder().serial(serial).build();
    }

}
