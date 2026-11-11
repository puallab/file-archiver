package com.filearchiver.packing.infrastructure.persistence.numbering;

import com.filearchiver.packing.domain.numbering.NumberCounter;
import com.filearchiver.packing.domain.numbering.NumberCounterId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumberCounterRepository extends JpaRepository<NumberCounter, NumberCounterId> {
}
