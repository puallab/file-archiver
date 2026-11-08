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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InspectionFileServiceTest {
    @Mock
    private InspectionFileRepository inspectionFileRepository;

    @Mock
    private ProductService productService;

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
        InspectionFileDto.RegisterRequest request = createRequest("inspection-files/SN-0001.txt");

        InspectionFile inspectionFile = createInspectionFile(product, request, givenId);

        given(productService.findOrCreateWithLock(request.serial())).willReturn(product);
        given(inspectionFileRepository.findByFilePath(request.filePath())).willReturn(Optional.empty());
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
    void 같은_filePath가_다시_오면_신규생성하지_않고_기존_id를_반환한다() {
        //given
        Long existingId = 1L;
        InspectionFileDto.RegisterRequest request = createRequest("inspection-files/SN-0001.txt");

        InspectionFile existingFile = createInspectionFile(product, request, existingId);

        given(productService.findOrCreateWithLock(request.serial())).willReturn(product);
        given(inspectionFileRepository.findByFilePath(request.filePath())).willReturn(Optional.of(existingFile));

        //when
        Long registeredId = inspectionFileService.register(request);

        //then
        verify(inspectionFileRepository, never()).save(any(InspectionFile.class));
        assertThat(registeredId).isEqualTo(existingId);
    }

    @Test
    void 등록된_제품으로_파일등록_요청이오면_파일_비교한다() {
        //given
        Long latestId = 1L;
        Long newId = 2L;
        InspectionFileDto.RegisterRequest latestRequest = createRequest("inspection-files/SN-0001-a.txt");
        InspectionFileDto.RegisterRequest newRequest = createRequest("inspection-files/SN-0001-b.txt");

        InspectionFile latestFile = createInspectionFile(product, latestRequest, latestId);
        InspectionFile newFile = createInspectionFile(product, newRequest, newId);

        given(productService.findOrCreateWithLock(newRequest.serial())).willReturn(product);
        given(inspectionFileRepository.findByFilePath(newRequest.filePath())).willReturn(Optional.empty());
        given(inspectionFileRepository.save(any(InspectionFile.class))).willReturn(newFile);
        given(product.getFileId()).willReturn(latestId);
        given(inspectionFileRepository.findById(latestId)).willReturn(Optional.of(latestFile));

        //when
        Long registeredId = inspectionFileService.register(newRequest);

        //then
        verify(product).applyInspectionFile(latestFile, newFile);
        assertThat(registeredId).isEqualTo(newId);
    }

    private InspectionFileDto.RegisterRequest createRequest(String filePath) {
        return new InspectionFileDto.RegisterRequest(
                "SN-0001",
                LocalDateTime.now().minusMinutes(1),
                "SENSOR-01",
                "EQ-07",
                filePath
        );
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
