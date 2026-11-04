package com.filearchiver.packing.infrastructure.persistence;

import com.filearchiver.TestcontainersConfiguration;
import com.filearchiver.packing.domain.InspectionFile;
import com.filearchiver.packing.domain.Product;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
public class InspectionFileRepositoryTest {
    @Autowired
    private InspectionFileRepository inspectionFileRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void 동일제품_동일_검사시간_등록시_실패(){
        Product product = Product.create("SN-0001");
        em.persistAndFlush(product);

        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(1);

        InspectionFile inspectionFile = InspectionFile.create(
                product,
                inspectedAt,
                "SENSOR-01",
                "EQ-07",
                "inspection-files/SN-0001.txt"
        );

        inspectionFileRepository.save(inspectionFile);
        em.flush();
        em.clear();

        //when
        InspectionFile newInspectionFile = InspectionFile.create(
                product,
                inspectedAt,
                "SENSOR-02",
                "EQ-09",
                "inspection-files/SN-0002.txt"
        );

        Assertions.assertThatThrownBy(() ->{
            inspectionFileRepository.saveAndFlush(newInspectionFile);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
