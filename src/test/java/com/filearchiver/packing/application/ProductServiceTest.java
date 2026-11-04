package com.filearchiver.packing.application;

import com.filearchiver.packing.domain.Product;
import com.filearchiver.packing.infrastructure.persistence.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void 존재하는_제품의_경우_반환(){
        //given
        String serial = "SN-001";
        Product product = Product.create(serial);

        given(productRepository.findWithLockBySerial(serial)).willReturn(Optional.of(product));

        //when
        Product result = productService.findOrCreateWithLock(serial);

        //then
        assertThat(result).isSameAs(product);

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void 존재하지_않는_제품의_경우_생성해서_반환(){
        //given
        String serial = "SN-001";
        Product product = Product.create(serial);

        given(productRepository.findWithLockBySerial(serial)).willReturn(Optional.empty());
        given(productRepository.save(any(Product.class))).willReturn(product);

        //when
        Product result = productService.findOrCreateWithLock(serial);

        //then
        assertThat(result.getSerial()).isEqualTo(serial);
        verify(productRepository).save(any(Product.class));
    }
}
