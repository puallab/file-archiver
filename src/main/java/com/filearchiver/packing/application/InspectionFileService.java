package com.filearchiver.packing.application;

import com.filearchiver.packing.domain.InspectionFile;
import com.filearchiver.packing.domain.Product;
import com.filearchiver.packing.infrastructure.persistence.InspectionFileRepository;
import com.filearchiver.packing.presentation.dto.InspectionFileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InspectionFileService {
    private final InspectionFileRepository inspectionFileRepository;
    private final ProductService productService;

    @Transactional
    public Long register(InspectionFileDto.RegisterRequest request) {

        Product product = productService.registerIfAbsent(request.serial());

        return inspectionFileRepository.findByProductAndInspectedAt(product, request.inspectedAt())
                .map(entity -> {

                    //이미 포장이 완료된 이후에 동일 검사시각으로 검사파일 등록 요청이 온 경우엔 무시한다.
                    if (product.isPacked()) {
                        return entity.getId();
                    }

                    /**
                     *  이 사이는 어떻게 방어?
                     */

                    //포장이 되지 않은 상태라면 뒤늦게 올라온 검사 파일을 최신으로 바라본다.
                    entity.updateInfo(
                            request.sensorId(),
                            request.uploaderEquipment(),
                            request.filePath()
                    );

                    return entity.getId();
                }).orElseGet(() -> {
                    InspectionFile inspectionFile = InspectionFile.create(
                            product,
                            request.inspectedAt(),
                            request.sensorId(),
                            request.uploaderEquipment(),
                            request.filePath()
                    );

                    return inspectionFileRepository.save(inspectionFile).getId();
                });
    }
}
