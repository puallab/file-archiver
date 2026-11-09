package com.filearchiver.packing.application;

import com.filearchiver.TestcontainersConfiguration;
import com.filearchiver.packing.domain.Packaging;
import com.filearchiver.packing.domain.Product;
import com.filearchiver.packing.domain.exception.PackagingRejectedException;
import com.filearchiver.packing.infrastructure.persistence.InspectionFileRepository;
import com.filearchiver.packing.infrastructure.persistence.PackagingRepository;
import com.filearchiver.packing.infrastructure.persistence.ProductRepository;
import com.filearchiver.packing.infrastructure.persistence.numbering.NumberCounterRepository;
import com.filearchiver.packing.presentation.dto.InspectionFileDto;
import com.filearchiver.packing.presentation.dto.PackagingDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class PackagingServiceIntegrationTest {
    @Autowired
    private InspectionFileService inspectionFileService;

    @Autowired
    private InspectionFileRepository inspectionFileRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private NumberCounterRepository numberCounterRepository;


    @Autowired
    private PackagingRepository packagingRepository;
    @Autowired
    private PackagingService packagingService;


    @BeforeEach
    void init(){
        //product 추가.
        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(1);
        for (int i = 1; i <= 10; i++) {
            String serial = String.format("SN-%04d", i);
            inspectionFileService.register(
                    new InspectionFileDto.RegisterRequest(
                            serial,
                            inspectedAt,
                            "SENSOR-01",
                            "EQ-07",
                            "inspection-files/" + serial + ".txt"
                    )
            );
        }
    }

    @AfterEach
    void deleteAllData(){
        inspectionFileRepository.deleteAll();
        productRepository.deleteAll();
        numberCounterRepository.deleteAll();
        packagingRepository.deleteAll();
    }

    @Test
    void 정상등록_테스트(){
        //given
        PackagingDto.PackRequest request = new PackagingDto.PackRequest(
                List.of("SN-0001", "SN-0002", "SN-0003")
        );

        //when
        PackagingDto.PackResponse response = packagingService.pack(request);

        //then
        Assertions.assertThat(response.packagingNo()).isNotNull();
        Assertions.assertThat(response.packagingTime()).isNotNull();

        Assertions.assertThat(numberCounterRepository.count()).isEqualTo(1);
        Assertions.assertThat(packagingRepository.count()).isEqualTo(1);


        Packaging packaging = packagingRepository.findAll().get(0);
        Assertions.assertThat(packaging.getPackagingNo()).isEqualTo(response.packagingNo());

        List<Product> products = productRepository.findBySerialIn(request.serials());
        Assertions.assertThat(products).hasSize(request.serials().size());
        Assertions.assertThat(products)
                .allMatch(product -> packaging.getId().equals(product.getPackaging().getId()));

    }

    @Test
    void 이미_포장된_제품이_포함되면_거절한다(){
        //given
        packagingService.pack(new PackagingDto.PackRequest(List.of("SN-0003")));
        Long firstBoxId = packagingRepository.findAll().get(0).getId();

        PackagingDto.PackRequest request = new PackagingDto.PackRequest(
                List.of("SN-0001", "SN-0002", "SN-0003")
        );

        //when & then
        Assertions.assertThatThrownBy(() -> packagingService.pack(request))
                .isInstanceOf(PackagingRejectedException.class);

        Assertions.assertThat(packagingRepository.count()).isEqualTo(1);
        Assertions.assertThat(productRepository.findBySerialIn(List.of("SN-0001", "SN-0002")))
                .hasSize(2)
                .allMatch(product -> product.getPackaging() == null);

        Product alreadyPacked = productRepository.findBySerialIn(List.of("SN-0003")).get(0);
        Assertions.assertThat(alreadyPacked.getPackaging().getId()).isEqualTo(firstBoxId);
    }

    @Test
    void 존재하지_않는_제품_포장시_거절한다(){
        //given
        PackagingDto.PackRequest request = new PackagingDto.PackRequest(
                List.of("SN-0001", "SN-9999")
        );

        //when & then
        Assertions.assertThatThrownBy(() -> packagingService.pack(request))
                .isInstanceOf(PackagingRejectedException.class);

        Assertions.assertThat(packagingRepository.count()).isZero();
        Assertions.assertThat(productRepository.findBySerialIn(List.of("SN-0001")))
                .hasSize(1)
                .allMatch(product -> product.getPackaging() == null);
    }
}
