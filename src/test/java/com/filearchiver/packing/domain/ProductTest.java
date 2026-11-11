package com.filearchiver.packing.domain;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void 포장된_제품은_파일이_바뀌지_않는다() {
        // given
        Product product = Product.create("SN-0001");
        LocalDateTime base = LocalDateTime.now().minusMinutes(10);
        InspectionFile latestFile = createFile(product, base, "a.txt", 1L);
        InspectionFile newFile = createFile(product, base.plusMinutes(1), "b.txt", 2L);
        product.applyInspectionFile(null, latestFile);
        product.pack(Packaging.createBox("BOX-0001"));

        // when
        product.applyInspectionFile(latestFile, newFile);

        // then
        assertThat(product.getFileId()).isEqualTo(1L);
    }

    @Test
    void 기존_파일이_없으면_신규_파일로_등록한다() {
        // given
        Product product = Product.create("SN-0001");
        InspectionFile newFile = createFile(product, LocalDateTime.now().minusMinutes(10), "a.txt", 1L);

        // when
        product.applyInspectionFile(null, newFile);

        // then
        assertThat(product.getFileId()).isEqualTo(1L);
    }

    @Test
    void 신규등록_파일이_최근_검사파일이면_신규_파일로_변경한다() {
        // given
        Product product = Product.create("SN-0001");
        LocalDateTime base = LocalDateTime.now().minusMinutes(10);
        InspectionFile latestFile = createFile(product, base, "a.txt", 1L);
        InspectionFile newFile = createFile(product, base.plusMinutes(1), "b.txt", 2L);
        product.applyInspectionFile(null, latestFile);

        // when
        product.applyInspectionFile(latestFile, newFile);

        // then
        assertThat(product.getFileId()).isEqualTo(2L);
    }

    @Test
    void 기존_파일이_최근_검사파일이면_기존_파일이_유지된다() {
        // given
        Product product = Product.create("SN-0001");
        LocalDateTime base = LocalDateTime.now().minusMinutes(10);
        InspectionFile latestFile = createFile(product, base.plusMinutes(1), "a.txt", 1L);
        InspectionFile newFile = createFile(product, base, "b.txt", 2L);
        product.applyInspectionFile(null, latestFile);

        // when
        product.applyInspectionFile(latestFile, newFile);

        // then
        assertThat(product.getFileId()).isEqualTo(1L);
    }

    private InspectionFile createFile(Product product, LocalDateTime inspectedAt, String filePath, Long id) {
        InspectionFile file = InspectionFile.create(
            product,
            inspectedAt,
            "SENSOR-01",
            "EQ-07",
            filePath
        );
        ReflectionTestUtils.setField(file, "id", id);
        return file;
    }
}
