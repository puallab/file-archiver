package com.filearchiver.packing.domain.numbering;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class NumberRule {
    @Id
    @Enumerated(EnumType.STRING)
    private NumberRuleType rule;

    @Builder(access = AccessLevel.PRIVATE)
    private NumberRule(NumberRuleType rule){
        this.rule = rule;
    }

    public static NumberRule createRule(NumberRuleType rule){
        return NumberRule.builder()
                .rule(rule)
                .build();
    }

    public String convertFormat(LocalDate date, int value){
        return this.rule.convertFormat(date, value);
    }
}
