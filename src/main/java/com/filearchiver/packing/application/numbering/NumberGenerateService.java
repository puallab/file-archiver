package com.filearchiver.packing.application.numbering;

import com.filearchiver.packing.domain.numbering.NumberCounter;
import com.filearchiver.packing.domain.numbering.NumberCounterId;
import com.filearchiver.packing.domain.numbering.NumberRule;
import com.filearchiver.packing.domain.numbering.NumberRuleType;
import com.filearchiver.packing.infrastructure.persistence.numbering.NumberCounterRepository;
import com.filearchiver.packing.infrastructure.persistence.numbering.NumberRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NumberGenerateService {
    private final NumberRuleRepository numberRuleRepository;
    private final NumberCounterRepository numberCounterRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String nextSequenceNumber(NumberRuleType ruleType) {

        NumberRule numberRule = numberRuleRepository.findByIdWithLock(ruleType)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 Numbering 규칙입니다."));

        //당일 발급된 id 조회(없으면 신규 발급)해서 value 증가.
        NumberCounterId todayId = NumberCounterId.todayId(ruleType);
        NumberCounter numberCounter = numberCounterRepository.findById(todayId)
                .orElseGet(() -> numberCounterRepository.save(NumberCounter.start(todayId)))
                .increaseLastValue();

        return numberRule.convertFormat(numberCounter.getIssuedDate(), numberCounter.getLastValue());
    }
}
