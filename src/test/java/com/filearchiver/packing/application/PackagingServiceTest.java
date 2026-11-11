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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PackagingServiceTest {

    @Mock
    private PackagingRepository packagingRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NumberGenerateService numberGenerateService;

    @InjectMocks
    private PackagingService packagingService;

    @Test
    void 포장_중복_시리얼_요청시_에외_발생() {
        //given
        PackagingDto.PackRequest request = new PackagingDto.PackRequest(
                List.of("SN-0001", "SN-0002", "SN-0001")
        );
        //when then
        assertThatThrownBy(() -> packagingService.pack(request)).isInstanceOf(DuplicateSerialInputException.class);

        verify(numberGenerateService, never()).nextSequenceNumber(NumberRuleType.PACKAGING_NO);
        verify(packagingRepository, never()).save(any(Packaging.class));
        verify(productRepository, never()).updatePackagingBySerials(any(Packaging.class), anyList());
    }

    @Test
    void 포장요청_정상_처리() {
        //given
        PackagingDto.PackRequest request = new PackagingDto.PackRequest(
                List.of("SN-0001", "SN-0002", "SN-0003")
        );
        String boxNo = "20260608-001";

        given(numberGenerateService.nextSequenceNumber(NumberRuleType.PACKAGING_NO)).willReturn(boxNo);
        given(productRepository.updatePackagingBySerials(any(Packaging.class), anyList())).willReturn(request.serials().size());

        //when
        PackagingDto.PackResponse pack = packagingService.pack(request);

        //then
        assertThat(pack.packagingNo()).isEqualTo(boxNo);
        verify(numberGenerateService).nextSequenceNumber(NumberRuleType.PACKAGING_NO);
        verify(packagingRepository).save(any(Packaging.class));
        verify(productRepository).updatePackagingBySerials(any(Packaging.class), anyList());
    }

    @Test
    void 존재하지_않는_시리얼이_있으면_포장을_거절한다() {
        //given
        PackagingDto.PackRequest request = new PackagingDto.PackRequest(
                List.of("SN-0001", "SN-9999")
        );

        given(numberGenerateService.nextSequenceNumber(NumberRuleType.PACKAGING_NO)).willReturn("20260608-001");
        given(productRepository.updatePackagingBySerials(any(Packaging.class), anyList())).willReturn(1);
        given(productRepository.findBySerialIn(anyList())).willReturn(List.of(Product.create("SN-0001")));
        given(productRepository.findByPackagingIsNotAndSerialIn(any(Packaging.class), anyList())).willReturn(List.of());

        //when then
        assertThatThrownBy(() -> packagingService.pack(request)).isInstanceOf(PackagingRejectedException.class);
    }

    @Test
    void 이미_포장된_시리얼이_있으면_포장을_거절한다() {
        //given
        PackagingDto.PackRequest request = new PackagingDto.PackRequest(
                List.of("SN-0001", "SN-0002")
        );
        Product packedProduct = Product.create("SN-0002");
        packedProduct.pack(Packaging.createBox("20260607-001"));

        given(numberGenerateService.nextSequenceNumber(NumberRuleType.PACKAGING_NO)).willReturn("20260608-001");
        given(productRepository.updatePackagingBySerials(any(Packaging.class), anyList())).willReturn(1);
        given(productRepository.findBySerialIn(anyList())).willReturn(List.of(Product.create("SN-0001"), packedProduct));
        given(productRepository.findByPackagingIsNotAndSerialIn(any(Packaging.class), anyList())).willReturn(List.of(packedProduct));

        //when then
        assertThatThrownBy(() -> packagingService.pack(request)).isInstanceOf(PackagingRejectedException.class);
    }
}
