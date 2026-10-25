package com.filearchiver.numbering.infrastructure.init;

import com.filearchiver.numbering.domain.NumberRuleType;
import com.filearchiver.numbering.service.NumberRuleInitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NumberRuleInitializer {
    private final NumberRuleInitService ruleInitService;

    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultRule(){
        for(NumberRuleType ruleType : NumberRuleType.values()){
            try{
                ruleInitService.createDefaultRule(ruleType);
            }catch(DataIntegrityViolationException ignored){
                log.warn("Number Rule 기본 타입 중복 생성 예외 발생 rule = {}",ruleType.name());
            }
        }
    }
}
