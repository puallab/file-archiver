package com.filearchiver.packing.domain.numbering;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
public enum NumberRuleType {
    PACKAGING_NO(DateTimeFormatter.ofPattern("yyyyMMdd"), 6),
    SHIPMENT_NO(DateTimeFormatter.ofPattern("yyyyMMdd"), 3);

    private final DateTimeFormatter dateTimeFormatter;
    private final int digits;

    public String convertFormat(LocalDate date, int count) {

        if(String.valueOf(count).length() > this.digits){
            throw new IllegalStateException(
                    "발급 요청 번호가 최대 자리수를 초과하였습니다. rule = %s, digits = %d, count = %d".formatted(this.name(), this.digits, count)
            );
        }

        return date.format(this.dateTimeFormatter) +"-"+ String.format("%0" + this.digits + "d", count);
    }
}
