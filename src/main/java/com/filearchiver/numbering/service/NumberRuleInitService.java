package com.filearchiver.numbering.service;

import com.filearchiver.numbering.domain.NumberRule;
import com.filearchiver.numbering.domain.NumberRuleType;
import com.filearchiver.numbering.infrastructure.persistence.NumberRuleRepository;
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
