package com.filearchiver.packing.application;

import com.filearchiver.TestcontainersConfiguration;
import com.filearchiver.packing.domain.InspectionFile;
import com.filearchiver.packing.infrastructure.persistence.InspectionFileRepository;
import com.filearchiver.packing.infrastructure.persistence.ProductRepository;
import com.filearchiver.packing.presentation.dto.InspectionFileDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

@DataJpaTest
@Import({TestcontainersConfiguration.class, InspectionFileService.class, ProductService.class})
public class InspectionFileServiceTest {
    @Autowired
    private InspectionFileService inspectionFileService;

    @Autowired
    private InspectionFileRepository inspectionFileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void 검사파일이_없으면_신규_저장하고_id를_반환한다() {
        //given
        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(1);
        InspectionFileDto.RegisterRequest registerRequest = new InspectionFileDto.RegisterRequest(
                "SN-0001",
                inspectedAt,
                "SENSOR-01",
                "EQ-07",
                "inspection-files/SN-0001.txt"
        );

        //when
        Long fileId = inspectionFileService.register(registerRequest);

        //then
        InspectionFile savedInspectionFile = inspectionFileRepository.findById(fileId).orElseThrow();
        Assertions.assertThat(savedInspectionFile.getProduct().getSerial()).isEqualTo("SN-0001");
        Assertions.assertThat(savedInspectionFile.getInspectedAt()).isEqualTo(inspectedAt);
        Assertions.assertThat(savedInspectionFile.getSensorId()).isEqualTo("SENSOR-01");
        Assertions.assertThat(savedInspectionFile.getUploaderEquipment()).isEqualTo("EQ-07");
        Assertions.assertThat(savedInspectionFile.getFilePath()).isEqualTo("inspection-files/SN-0001.txt");
        Assertions.assertThat(productRepository.findBySerial("SN-0001").isPresent()).isTrue();
    }

    @Test
    void 같은_제품_다른_검사시각_등록시_검사파일_각각_생성(){
        //given
        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(2);

        Long firstFileId = inspectionFileService.register(
                new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt,
                        "SENSOR-01",
                        "EQ-07",
                        "inspection-files/SN-0001.txt"
                )
        );

        //when
        Long secondFileId = inspectionFileService.register(
                new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt.plusMinutes(1),
                        "SENSOR-02",
                        "EQ-08",
                        "inspection-files/SN-0002.txt"
                )
        );

        //then
        InspectionFile firstFile = inspectionFileRepository.findById(firstFileId).orElseThrow();
        InspectionFile secondFile = inspectionFileRepository.findById(secondFileId).orElseThrow();
        Assertions.assertThat(firstFile.getId()).isNotEqualTo(secondFile.getId());

        //제품은 1건만 등록되는지 확인
        Assertions.assertThat(productRepository.count()).isEqualTo(1L);
    }

    @Test
    void 포장_전_제품은_파일이_변경된다(){
        //given
        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(1);

        Long firstFileId = inspectionFileService.register(
                new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt,
                        "SENSOR-01",
                        "EQ-07",
                        "inspection-files/SN-0001.txt"
                )
        );

        //when
        Long secondFileId = inspectionFileService.register(
                new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt,
                        "SENSOR-02",
                        "EQ-08",
                        "inspection-files/SN-0002.txt"
                )
        );

        //modifying clearAutomatically = false일 경우에
        //영속성 컨텍스트에서 값을 조회해오므로 변경되지 않은 값을 조회하게됨.
        //따라서 강제로 영속성 컨텍스트 비워주어 변경된 값으로 DB에서 조회해올 수 있도록 한다.
        em.flush();
        em.clear();

        //then
        InspectionFile firstFile = inspectionFileRepository.findById(firstFileId).orElseThrow();
        InspectionFile secondFile = inspectionFileRepository.findById(secondFileId).orElseThrow();
        Assertions.assertThat(firstFile.getId()).isEqualTo(secondFile.getId());

        Assertions.assertThat(secondFile.getSensorId()).isEqualTo("SENSOR-02");
        Assertions.assertThat(secondFile.getUploaderEquipment()).isEqualTo("EQ-08");
        Assertions.assertThat(secondFile.getFilePath()).isEqualTo("inspection-files/SN-0002.txt");

        Assertions.assertThat(inspectionFileRepository.count()).isEqualTo(1L);
    }

    @Test
    void 포장된_제품은_파일이_변경되지_않는다(){
        //given
        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(1);

        Long firstFileId = inspectionFileService.register(
                new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt,
                        "SENSOR-01",
                        "EQ-07",
                        "inspection-files/SN-0001.txt"
                )
        );
        em.flush();

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
                        where serial = 'SN-0001'
                        """)
                .executeUpdate();
        em.clear();

        //when
        Long secondFileId = inspectionFileService.register(
                new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt,
                        "SENSOR-02",
                        "EQ-08",
                        "inspection-files/SN-0002.txt"
                )
        );
        em.flush();
        em.clear();

        //then
        Assertions.assertThat(secondFileId).isEqualTo(firstFileId);

        InspectionFile file = inspectionFileRepository.findById(firstFileId).orElseThrow();
        Assertions.assertThat(file.getSensorId()).isEqualTo("SENSOR-01");
        Assertions.assertThat(file.getUploaderEquipment()).isEqualTo("EQ-07");
        Assertions.assertThat(file.getFilePath()).isEqualTo("inspection-files/SN-0001.txt");

        Assertions.assertThat(inspectionFileRepository.count()).isEqualTo(1L);
    }
}
