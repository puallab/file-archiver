package com.filearchiver.numbering.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
public enum NumberRuleType {
    PACKAGING_NO("yyyyMMdd", 6),
    SHIPMENT_NO("yyyyMMdd", 3);

    private final String datePattern;
    private final int digits;

    public String convertFormat(LocalDate date, int count) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(this.datePattern);
        return date.format(dateTimeFormatter) +"-"+ String.format("%0" + this.digits + "d", count);
    }
}
