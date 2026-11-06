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

        Product product = productService.findOrCreateWithLock(request.serial());

        InspectionFile inspectionFile = inspectionFileRepository.findByProductAndInspectedAt(product, request.inspectedAt())
                .map(findInspectionFile -> {

                    //이미 포장이 완료된 이후에 동일 검사시각으로 검사파일 등록 요청이 온 경우엔 무시한다.
                    if (product.isPacked()) {
                        return findInspectionFile;
                    }

                    findInspectionFile.updateInfo(request.sensorId(), request.uploaderEquipment(), request.filePath());

                    return findInspectionFile;
                }).orElseGet(() -> {
                    InspectionFile newFile = InspectionFile.create(
                            product,
                            request.inspectedAt(),
                            request.sensorId(),
                            request.uploaderEquipment(),
                            request.filePath()
                    );

                    return inspectionFileRepository.save(newFile);
                });

        //최신 fileId로 product에 넣어준다.
        if (product.getFileId() != null) {
            InspectionFile latestFile = inspectionFileRepository.findById(product.getFileId()).orElseGet(()-> inspectionFile);

            product.updateLatestFileId(inspectionFile.getLatestWith(latestFile).getId());
        }

        return product.getFileId();
    }

    private Long getLatestFileId(Product product, InspectionFile inspectionFile){
        if(product.getFileId() != null){
            InspectionFile latestFile = inspectionFileRepository.findById(product.getFileId()).orElseThrow();

            product.updateLatestFileId(inspectionFile.getLatestWith(latestFile).getId());
            return latestFile.getId();
        }else{
            return null;
        }
    }
}
