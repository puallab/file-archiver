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

@Service
@RequiredArgsConstructor
public class PackagingService {
    private final PackagingRepository packagingRepository;
    private final ProductRepository productRepository;
    private final NumberGenerateService numberGenerator;

    @Transactional
    public PackagingDto.PackResponse pack(PackagingDto.PackRequest request){

        //0.중복 입력 검사
        Set<String> set = new HashSet<>(request.serials());
        if(set.size() != request.serials().size()){
            throw new DuplicateSerialInputException();
        }

        Packaging box = Packaging.createBox(numberGenerator.nextSequenceNumber(NumberRuleType.PACKAGING_NO));

        //시리얼 중 검사 파일 없으면 거절, 하나라도 포장되어 있으면 거절
        //product fileId가 없거나. 조회 결과가 전체 수랑 다름.
        //product에 packaging id가 이미 존재함.
        List<Product> products = productRepository.findBySerialIn(request.serials())
                .orElseGet(List::of)
                .stream()
                .map(product -> {

                    if (product.isPacked()) {
                        throw new DuplicatePackAttemptException();
                    }

                    product.pack(box);

                    return product;
                })
                .toList();

        if(products.size() != set.size()){
            throw new IllegalStateException("검사 파일이 존재하지 않는 Production이 존재합니다.");
        }

        packagingRepository.save(box);

        return new PackagingDto.PackResponse(box.getPackagingNo(), LocalDateTime.now());
    }
}
