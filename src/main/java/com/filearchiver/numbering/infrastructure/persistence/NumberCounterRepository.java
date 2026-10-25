package com.filearchiver.numbering.infrastructure.persistence;

import com.filearchiver.numbering.domain.NumberCounter;
import com.filearchiver.numbering.domain.NumberCounterId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumberCounterRepository extends JpaRepository<NumberCounter, NumberCounterId> {
}
