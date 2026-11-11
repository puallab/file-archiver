package com.filearchiver.packing.infrastructure.persistence;

import com.filearchiver.packing.domain.Packaging;
import com.filearchiver.packing.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findWithLockBySerial(String serial);

    @Modifying(clearAutomatically = true)
    @Query("""
           update Product p
           set p.packaging = :packing
           where p.serial in (:serials)
               and p.packaging is null
    """)
    int updatePackagingBySerials(@Param("packing")Packaging packaging, @Param("serials") List<String> serials);

    List<Product> findBySerialIn(List<String> serials);

    List<Product> findByPackagingIsNotAndSerialIn(Packaging packaging, List<String> serials);
}
