package com.filearchiver.packing.application;

import com.filearchiver.packing.application.numbering.NumberGenerateService;
import com.filearchiver.packing.domain.Packaging;
import com.filearchiver.packing.domain.Product;
import com.filearchiver.packing.domain.exception.DuplicateSerialInputException;
import com.filearchiver.packing.domain.exception.PackagingRejectedException;
import com.filearchiver.packing.domain.numbering.NumberRuleType;
import com.filearchiver.packing.infrastructure.persistence.PackagingRepository;
import com.filearchiver.packing.infrastructure.persistence.ProductRepository;
import com.filearchiver.packing.presentation.dto.PackagingDto;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackagingService {
    private final PackagingRepository packagingRepository;
    private final ProductRepository productRepository;
    private final NumberGenerateService numberGenerator;

    @Transactional
    public PackagingDto.PackResponse pack(PackagingDto.PackRequest request) {

        //0.중복 입력 검사
        Set<String> set = new HashSet<>(request.serials());
        if (set.size() != request.serials().size()) {
            throw new DuplicateSerialInputException();
        }

        Packaging box = Packaging.createBox(numberGenerator.nextSequenceNumber(NumberRuleType.PACKAGING_NO));
        packagingRepository.save(box);

        //시리얼 중 검사 파일 없으면 거절, 하나라도 포장되어 있으면 거절
        //product에 packaging id가 이미 존재함.
        int updatedRowCount = productRepository.updatePackagingBySerials(box, request.serials());

        //1.존재하지 않는 serial이 들어올 수 있음.
        //2.이미 포장된 serial이 포함될 수 있음
        if (updatedRowCount != set.size()) {
            throw new PackagingRejectedException(getNonexistentSerialList(request), getAlreadyPackedSerial(request, box));
        }

        return new PackagingDto.PackResponse(box.getPackagingNo(), LocalDateTime.now());
    }

    private String getAlreadyPackedSerial(PackagingDto.PackRequest request, Packaging box) {
        List<Product> alreadyPackedList = productRepository.findByPackagingIsNotAndSerialIn(box, request.serials());
        return alreadyPackedList.stream()
                .map(Product::getSerial)
                .collect(Collectors.joining(","));
    }

    private @NonNull String getNonexistentSerialList(PackagingDto.PackRequest request) {
        Set<String> existSerialSet = productRepository.findBySerialIn(request.serials())
                .stream()
                .map(Product::getSerial)
                .collect(Collectors.toSet());

        return request.serials()
                .stream()
                .filter(requestSerial -> !existSerialSet.contains(requestSerial))
                .collect(Collectors.joining(","));
    }
}
