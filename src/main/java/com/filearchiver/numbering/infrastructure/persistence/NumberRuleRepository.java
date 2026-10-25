package com.filearchiver.numbering.infrastructure.persistence;

import com.filearchiver.numbering.domain.NumberRule;
import com.filearchiver.numbering.domain.NumberRuleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NumberRuleRepository extends JpaRepository<NumberRule, NumberRuleType> {

    //rule별로 number 생성시 rule 테이블에 Lock 걸어서 한건 씩 처리할 수 있도록 한다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select n from NumberRule n where n.rule = :rule")
    Optional<NumberRule> findByIdWithLock(@Param("rule") NumberRuleType rule);

}
