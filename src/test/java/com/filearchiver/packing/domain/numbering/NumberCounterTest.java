package com.filearchiver.packing.domain.numbering;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class NumberCounterTest {
    private final NumberRuleType ruleTypePack = NumberRuleType.PACKAGING_NO;
    private final NumberRuleType ruleTypeShip = NumberRuleType.SHIPMENT_NO;

    @Test
    void 신규_카운터는_0부터_시작한다() {
        //given
        NumberCounterId numberCounterId = NumberCounterId.todayId(ruleTypePack);

        //when
        NumberCounter start = NumberCounter.start(numberCounterId);

        //then
        assertThat(start)
                .returns(0, NumberCounter::getLastValue)
                .returns(LocalDate.now(), NumberCounter::getIssuedDate);
    }

    @Test
    void 증가시_마지막_번호에서_1_오른다() {
        //given
        NumberCounterId numberCounterId = NumberCounterId.todayId(ruleTypePack);
        NumberCounter start = NumberCounter.start(numberCounterId);
        int lastValue = start.getLastValue();

        //when
        start.increaseLastValue();

        //then
        assertThat(start)
                .returns(lastValue + 1, NumberCounter::getLastValue)
                .returns(LocalDate.now(), NumberCounter::getIssuedDate);
    }
}
