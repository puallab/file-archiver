package com.filearchiver.packing.application.numbering;

import com.filearchiver.packing.domain.numbering.NumberRule;
import com.filearchiver.packing.domain.numbering.NumberRuleType;
import com.filearchiver.packing.infrastructure.persistence.numbering.NumberRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NumberRuleInitService {

    private final NumberRuleRepository numberRuleRepository;

    @Transactional
    public void createDefaultRule(NumberRuleType ruleType) {
        if(numberRuleRepository.existsById(ruleType)){
           return;
        }

        numberRuleRepository.saveAndFlush(NumberRule.createRule(ruleType));
    }
}
