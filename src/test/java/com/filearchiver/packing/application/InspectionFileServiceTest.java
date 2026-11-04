package com.filearchiver.packing.application;

import com.filearchiver.packing.domain.InspectionFile;
import com.filearchiver.packing.domain.Product;
import com.filearchiver.packing.infrastructure.persistence.InspectionFileRepository;
import com.filearchiver.packing.presentation.dto.InspectionFileDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InspectionFileServiceTest {
    @Mock
    private InspectionFileRepository inspectionFileRepository;

    @Mock
    private ProductService productService;

    //packaging entity를 주입할 서비스가 아직 개발되지 않아서 우선 mock으로 Product 주입.
    @Mock
    private Product product;

    @InjectMocks
    private InspectionFileService inspectionFileService;

    @Captor
    private ArgumentCaptor<InspectionFile> inspectionFileCaptor;

    @Test
    void 검사파일이_없으면_신규_생성하고_id를_반환한다() {
        //given
        Long givenId = 1L;
        LocalDateTime inspectedAt = LocalDateTime.now();
        InspectionFileDto.RegisterRequest request = new InspectionFileDto.RegisterRequest(
                "SN-0001",
                inspectedAt,
                "SENSOR-01",
                "EQ-07",
                "inspection-files/SN-0001.txt"
        );

        InspectionFile inspectionFile = createInspectionFile(product, request, givenId);

        given(productService.findOrCreateWithLock(request.serial())).willReturn(product);
        given(inspectionFileRepository.findByProductAndInspectedAt(product, request.inspectedAt())).willReturn(Optional.empty());
        given(inspectionFileRepository.save(any(InspectionFile.class))).willReturn(inspectionFile);

        //when
        Long registeredId = inspectionFileService.register(request);

        //then
        verify(inspectionFileRepository).save(inspectionFileCaptor.capture());

        InspectionFile capturedInspectionFile = inspectionFileCaptor.getValue();

        assertThat(capturedInspectionFile)
                .returns(product, InspectionFile::getProduct)
                .returns(request.inspectedAt(), InspectionFile::getInspectedAt)
                .returns(request.sensorId(), InspectionFile::getSensorId)
                .returns(request.uploaderEquipment(), InspectionFile::getUploaderEquipment)
                .returns(request.filePath(), InspectionFile::getFilePath);

        assertThat(registeredId).isEqualTo(givenId);
    }

    @Test
    void 포장전_제품은_파일이_변경된다(){
        //given
        Long givenId = 1L;
        LocalDateTime inspectedAt = LocalDateTime.now();
        InspectionFileDto.RegisterRequest firstRequest = new InspectionFileDto.RegisterRequest(
                "SN-0001",
                inspectedAt,
                "SENSOR-01",
                "EQ-07",
                "inspection-files/SN-0001.txt"
        );

        InspectionFile inspectionFile = createInspectionFile(product, firstRequest, givenId);

        given(productService.findOrCreateWithLock(firstRequest.serial())).willReturn(product);
        given(inspectionFileRepository.findByProductAndInspectedAt(product, firstRequest.inspectedAt())).willReturn(Optional.of(inspectionFile));

        //when
        InspectionFileDto.RegisterRequest secondRequest = new InspectionFileDto.RegisterRequest(
                "SN-0001",
                inspectedAt,
                "SENSOR-02",
                "EQ-08",
                "inspection-files/SN-0002.txt"
        );
        Long registeredId = inspectionFileService.register(secondRequest);

        //then
        assertThat(registeredId).isEqualTo(givenId);
        assertThat(inspectionFile)
                .returns(secondRequest.sensorId(), InspectionFile::getSensorId)
                .returns(secondRequest.uploaderEquipment(), InspectionFile::getUploaderEquipment)
                .returns(secondRequest.filePath(), InspectionFile::getFilePath);
    }

    @Test
    void 포장된_제품은_파일이_변경되지_않는다(){
        //given
        Long givenId = 1L;
        LocalDateTime inspectedAt = LocalDateTime.now();
        InspectionFileDto.RegisterRequest firstRequest = new InspectionFileDto.RegisterRequest(
                "SN-0001",
                inspectedAt,
                "SENSOR-01",
                "EQ-07",
                "inspection-files/SN-0001.txt"
        );

        InspectionFile inspectionFile = createInspectionFile(product, firstRequest, givenId);

        given(productService.findOrCreateWithLock(firstRequest.serial())).willReturn(product);
        given(inspectionFileRepository.findByProductAndInspectedAt(product, firstRequest.inspectedAt())).willReturn(Optional.of(inspectionFile));
        given(product.isPacked()).willReturn(true);

        //when
        InspectionFileDto.RegisterRequest secondRequest = new InspectionFileDto.RegisterRequest(
                "SN-0001",
                inspectedAt,
                "SENSOR-02",
                "EQ-08",
                "inspection-files/SN-0002.txt"
        );
        Long registeredId = inspectionFileService.register(secondRequest);

        //then
        assertThat(registeredId).isEqualTo(givenId);
        assertThat(inspectionFile)
                .returns(firstRequest.sensorId(), InspectionFile::getSensorId)
                .returns(firstRequest.uploaderEquipment(), InspectionFile::getUploaderEquipment)
                .returns(firstRequest.filePath(), InspectionFile::getFilePath);
    }

    private InspectionFile createInspectionFile(
            Product product,
            InspectionFileDto.RegisterRequest registerRequest,
            Long id
    ) {
        InspectionFile inspectionFile = InspectionFile.create(
                product,
                registerRequest.inspectedAt(),
                registerRequest.sensorId(),
                registerRequest.uploaderEquipment(),
                registerRequest.filePath()
        );
        ReflectionTestUtils.setField(inspectionFile, "id", id);
        return inspectionFile;
    }
}
