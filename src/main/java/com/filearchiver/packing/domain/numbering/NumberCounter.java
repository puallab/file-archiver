package com.filearchiver.packing.domain.numbering;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NumberCounter {
    @EmbeddedId
    private NumberCounterId numberCounterId;

    private int lastValue;

    private NumberCounter(NumberCounterId numberCounterId){
        this.numberCounterId = numberCounterId;
        this.lastValue = 0;
    }

    public static NumberCounter start(NumberCounterId ruleId){
        return new NumberCounter(ruleId);
    }

    public void increaseLastValue(){
        ++lastValue;
    }

    public LocalDate getIssuedDate(){
        return numberCounterId.issuedDate();
    }
}
