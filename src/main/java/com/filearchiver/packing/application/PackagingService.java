package com.filearchiver.packing.application;

import com.filearchiver.packing.application.numbering.NumberGenerateService;
import com.filearchiver.packing.domain.Packaging;
import com.filearchiver.packing.domain.Product;
import com.filearchiver.packing.domain.exception.DuplicatePackAttemptException;
import com.filearchiver.packing.domain.exception.DuplicateSerialInputException;
import com.filearchiver.packing.domain.numbering.NumberRuleType;
import com.filearchiver.packing.infrastructure.persistence.PackagingRepository;
import com.filearchiver.packing.infrastructure.persistence.ProductRepository;
import com.filearchiver.packing.presentation.dto.PackagingDto;
import lombok.RequiredArgsConstructor;
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

        //시리얼 중 검사 파일 없으면 거절, 하나라도 포장되어 있으면 거
        //product에 packaging id가 이미 존재함.
        int updatedRowCount = productRepository.updatePackagingBySerials(box, request.serials());

        //1.잘못된 serial이 들어올 수 있음.
        //2.이미 포장된 serial이 포함될 수 있음
        if (updatedRowCount != set.size()) {
            List<Product> alreadyPackedList = productRepository.findByPackagingIsNotAndSerialIn(box, request.serials());

            //조회되는 product가 없다 -> 애초에 존재하지 않는 serial이 들어왔다.
            if (alreadyPackedList.isEmpty()) {
                throw new IllegalArgumentException("잘못된 serial이 전달됨");
            }

            throw new DuplicatePackAttemptException(alreadyPackedList.stream()
                    .map(Product::getSerial)
                    .collect(Collectors.joining(",")));
        }

        return new PackagingDto.PackResponse(box.getPackagingNo(), LocalDateTime.now());
    }
}
