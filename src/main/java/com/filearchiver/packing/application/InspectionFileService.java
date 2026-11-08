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

        InspectionFile newInspectionFile = inspectionFileRepository.findByFilePath(request.filePath())
                .map(findInspectionFile -> {
                    findInspectionFile.updateInfo(request.sensorId(), request.uploaderEquipment());
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


        InspectionFile latestInspectionFile = getLatestInspectionFile(product, newInspectionFile);

        product.applyInspectionFile(latestInspectionFile, newInspectionFile);

        return newInspectionFile.getId();
    }

    //product에 연결된 file이 존재하면 최신 검사파일로 반환한다. 검사파일이 존재하지 않으면 신규로 들어온 파일로 대체한다.
    private InspectionFile getLatestInspectionFile(Product product, InspectionFile newInspectionFile) {
        if(product.getFileId() != null){
            return inspectionFileRepository.findById(product.getFileId()).orElseGet(()-> newInspectionFile);
        }
        return newInspectionFile;
    }
}
