package com.filearchiver.packing.domain;

import com.filearchiver.packing.domain.exception.InvalidInspectionTimeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InspectionFileTest {

    @Test
    void 검사_시각이_과거면_검사_파일이_생성된다() {
        // given
        Product product = Product.create("SN-0001");
        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(1);

        // when
        InspectionFile inspectionFile = InspectionFile.create(
            product,
            inspectedAt,
            "SENSOR-01",
            "EQ-07",
            "inspection-files/SN-0001.txt"
        );

        // then
        assertThat(inspectionFile.getProduct()).isSameAs(product);
        assertThat(inspectionFile.getInspectedAt()).isEqualTo(inspectedAt);
        assertThat(inspectionFile.getSensorId()).isEqualTo("SENSOR-01");
        assertThat(inspectionFile.getUploaderEquipment()).isEqualTo("EQ-07");
        assertThat(inspectionFile.getFilePath()).isEqualTo("inspection-files/SN-0001.txt");
        assertThat(inspectionFile.getCreatedAt()).isNotNull();
    }

    @Test
    void 검사_시각이_미래면_검사_파일을_생성할_수_없다() {
        // given
        Product product = Product.create("SN-0001");
        LocalDateTime inspectedAt = LocalDateTime.now().plusMinutes(1);

        // when & then
        assertThatThrownBy(() -> InspectionFile.create(
            product,
            inspectedAt,
            "SENSOR-01",
            "EQ-07",
            "inspection-files/SN-0001.txt"
        ))
            .isInstanceOf(InvalidInspectionTimeException.class);
    }
}
