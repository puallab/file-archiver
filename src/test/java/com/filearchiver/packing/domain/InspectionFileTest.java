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

    @Test
    void 비교_대상이_없으면_원파일을_그대로_반환한다() {
        // given
        InspectionFile file = createFile(LocalDateTime.now().minusMinutes(10), "a.txt");

        // when
        InspectionFile latest = file.getLatestWith(null);

        // then
        assertThat(latest).isSameAs(file);
    }

    @Test
    void 타겟파일이_최근_검사파일이면_타겟파일을_반환한다() {
        // given
        LocalDateTime base = LocalDateTime.now().minusMinutes(10);
        InspectionFile file = createFile(base, "a.txt");
        InspectionFile target = createFile(base.plusMinutes(1), "b.txt");

        // when
        InspectionFile latest = file.getLatestWith(target);

        // then
        assertThat(latest).isSameAs(target);
    }

    @Test
    void 원파일이_최근_검사파일이면_원파일을_반환한다() {
        // given
        LocalDateTime base = LocalDateTime.now().minusMinutes(10);
        InspectionFile file = createFile(base.plusMinutes(1), "a.txt");
        InspectionFile target = createFile(base, "b.txt");

        // when
        InspectionFile latest = file.getLatestWith(target);

        // then
        assertThat(latest).isSameAs(file);
    }

    @Test
    void 검사_시각이_같으면_원파일을_반환한다() {
        // given
        LocalDateTime inspectedAt = LocalDateTime.now().minusMinutes(10);
        InspectionFile file = createFile(inspectedAt, "a.txt");
        InspectionFile target = createFile(inspectedAt, "b.txt");

        // when
        InspectionFile latest = file.getLatestWith(target);

        // then
        assertThat(latest).isSameAs(file);
    }

    private InspectionFile createFile(LocalDateTime inspectedAt, String filePath) {
        return InspectionFile.create(
            Product.create("SN-0001"),
            inspectedAt,
            "SENSOR-01",
            "EQ-07",
            filePath
        );
    }
}
