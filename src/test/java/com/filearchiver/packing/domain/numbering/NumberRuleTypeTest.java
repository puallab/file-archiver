package com.filearchiver.packing.domain.numbering;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberRuleTypeTest {

    private int generateNDigitNumber(int digits) {
        if (digits < 1 || digits > 9) {
            throw new IllegalArgumentException("int 타입 범위 내 자릿수는 1~9자리만 가능합니다.");
        }

        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits);

        return ThreadLocalRandom.current().nextInt(min, max);
    }

    private int getMinimumDigits() {
        return Arrays.stream(NumberRuleType.values())
                .mapToInt(NumberRuleType::getDigits)
                .min()
                .orElseThrow();
    }

    private int getMaximumDigits() {
        return Arrays.stream(NumberRuleType.values())
                .mapToInt(NumberRuleType::getDigits)
                .max()
                .orElseThrow();
    }

    @ParameterizedTest
    @EnumSource(NumberRuleType.class)
    void 타입별_넘버링_포맷팅_정상_처리(NumberRuleType ruleType) {
        //given
        LocalDate localDate = LocalDate.of(2026, 10, 30);
        int count = generateNDigitNumber(getMinimumDigits());
        //when
        String ruleNo = ruleType.convertFormat(localDate, count);

        //then
        String expectedFormat = "20261030-%%0%dd".formatted(ruleType.getDigits());
        assertThat(ruleNo).isEqualTo(expectedFormat.formatted(count));
    }

    @ParameterizedTest
    @EnumSource(NumberRuleType.class)
    void 타입별_넘버링_자릿수_초과시_포맷팅_불가(NumberRuleType ruleType) {
        //given
        LocalDate localDate = LocalDate.of(2026, 10, 30);
        int count = generateNDigitNumber(getMaximumDigits() + 1);

        //then
        assertThatThrownBy(() -> ruleType.convertFormat(localDate, count))
                .hasMessageStartingWith("발급 요청 번호가 최대 자리수를 초과하였습니다.");
    }
}
