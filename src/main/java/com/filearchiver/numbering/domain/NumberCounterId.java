package com.filearchiver.numbering.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.io.Serializable;
import java.time.LocalDate;

@Embeddable
public record NumberCounterId (
        @Enumerated(EnumType.STRING)
        @Column(name = "rule")
        NumberRuleType rule,

        @Column(name = "issued_date")
        LocalDate issuedDate
) implements Serializable {

    public static NumberCounterId todayId(NumberRuleType ruleType) {
        return new NumberCounterId(ruleType, LocalDate.now());
    }
}
