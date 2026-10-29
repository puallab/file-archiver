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
    void 검사_파일_저장_및_조회(){
        //given
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

        //when
        inspectionFileRepository.save(inspectionFile);
        em.flush();
        em.clear();

        //then
        InspectionFile foundInspectionFile = inspectionFileRepository.findByProductAndInspectedAt(product, inspectedAt).orElseThrow();

        Assertions.assertThat(foundInspectionFile.getProduct().getId()).isEqualTo(product.getId());
        Assertions.assertThat(foundInspectionFile.getInspectedAt()).isEqualTo(inspectedAt);
        Assertions.assertThat(foundInspectionFile.getSensorId()).isEqualTo("SENSOR-01");
        Assertions.assertThat(foundInspectionFile.getUploaderEquipment()).isEqualTo("EQ-07");
        Assertions.assertThat(foundInspectionFile.getFilePath()).isEqualTo("inspection-files/SN-0001.txt");
    }

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

    @Test
    void 포장_전_제품은_파일이_변경된다(){
        //given
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

        //when
        inspectionFileRepository.save(inspectionFile);
        em.flush();
        em.clear();

        int updatedRow = inspectionFileRepository.updateIfNotPacked(
                product,
                inspectedAt,
                "SENSOR-02",
                "EQ-09",
                "inspection-files/SN-0002.txt"
        );

        //then
        InspectionFile foundInspectionFile = inspectionFileRepository.findByProductAndInspectedAt(product, inspectedAt).orElseThrow();

        Assertions.assertThat(foundInspectionFile.getSensorId()).isEqualTo("SENSOR-02");
        Assertions.assertThat(foundInspectionFile.getUploaderEquipment()).isEqualTo("EQ-09");
        Assertions.assertThat(foundInspectionFile.getFilePath()).isEqualTo("inspection-files/SN-0002.txt");
        Assertions.assertThat(updatedRow).isEqualTo(1);
    }

    /**
     * 테스트 진행하려하니 아직 개발되지 않은 엔티티의 기능이 필요함.
     */
    @Test
    void 포장된_제품이면_검사_파일이_바뀌지_않는다(){
        //given
        Product product = Product.create("SN-0001");
        em.persistAndFlush(product);

        // 포장 기능이 아직 없어 포장 행과 제품 연결은 SQL로 넣는다
        em.getEntityManager()
                .createNativeQuery("""
                        insert into packaging (packaging_no, type, status, created_at)
                        values ('PKG-TEST-0001', 'BOX', 'PENDING', now())
                        """)
                .executeUpdate();
        em.getEntityManager()
                .createNativeQuery("""
                        update product
                        set packaging_id = (select id from packaging where packaging_no = 'PKG-TEST-0001')
                        where id = :productId
                        """)
                .setParameter("productId", product.getId())
                .executeUpdate();

        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(1);

        InspectionFile inspectionFile = InspectionFile.create(
                product,
                inspectedAt,
                "SENSOR-01",
                "EQ-07",
                "inspection-files/SN-0001.txt"
        );

        //when
        inspectionFileRepository.save(inspectionFile);
        em.flush();
        em.clear();

        int updatedRow = inspectionFileRepository.updateIfNotPacked(
                product,
                inspectedAt,
                "SENSOR-02",
                "EQ-09",
                "inspection-files/SN-0002.txt"
        );

        //then
        InspectionFile foundInspectionFile = inspectionFileRepository.findByProductAndInspectedAt(product, inspectedAt).orElseThrow();

        Assertions.assertThat(foundInspectionFile.getSensorId()).isEqualTo("SENSOR-01");
        Assertions.assertThat(foundInspectionFile.getUploaderEquipment()).isEqualTo("EQ-07");
        Assertions.assertThat(foundInspectionFile.getFilePath()).isEqualTo("inspection-files/SN-0001.txt");
        Assertions.assertThat(updatedRow).isEqualTo(0);
    }
}
