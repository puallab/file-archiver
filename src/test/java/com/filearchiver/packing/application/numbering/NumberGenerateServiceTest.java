package com.filearchiver.packing.application.numbering;

import com.filearchiver.TestcontainersConfiguration;
import com.filearchiver.packing.domain.numbering.NumberRuleType;
import com.filearchiver.packing.infrastructure.persistence.numbering.NumberCounterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({TestcontainersConfiguration.class})
class NumberGenerateServiceTest {
    @Autowired
    private NumberGenerateService numberGenerateService;

    @Autowired
    private NumberCounterRepository numberCounterRepository;

    @AfterEach
    void deleteAllNumberCount() {
        numberCounterRepository.deleteAll();
    }

    private int getSequence(String packNumber) {
        return Integer.parseInt(packNumber.split("-")[1]);
    }

    @ParameterizedTest
    @EnumSource(NumberRuleType.class)
    void 같은_타입의_번호_발급시_이전_번호에서_1증가(NumberRuleType ruleType) {
        //given
        String firstNo = numberGenerateService.nextSequenceNumber(ruleType);
        int firstSequence = getSequence(firstNo);
        //when
        String secondNumber = numberGenerateService.nextSequenceNumber(ruleType);
        int secondSequence = getSequence(secondNumber);
        //then
        assertThat(secondNumber).isNotEqualTo(firstNo);
        assertThat(secondSequence).isEqualTo(firstSequence + 1);
    }

    @Test
    void 다른_타입의_번호_발급시_번호_증가_없음() {
        //given
        for (NumberRuleType type : NumberRuleType.values()) {
            numberGenerateService.nextSequenceNumber(type);
        }

        //when
        List<String> list = new ArrayList<>();
        for (NumberRuleType type : NumberRuleType.values()) {
            list.add(numberGenerateService.nextSequenceNumber(type));
        }

        //then
        long count = list.stream()
                .mapToInt(this::getSequence)
                .distinct()
                .count();
        assertThat(count).isEqualTo(1);
    }

    @ParameterizedTest
    @EnumSource(NumberRuleType.class)
    void 같은_타입_동시에_발급시_순차적으로_발급(NumberRuleType ruleType) {
        //given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        //when
        List<Future<String>> futureList = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            futureList.add(executorService.submit(() -> {
                startLatch.await();
                return numberGenerateService.nextSequenceNumber(ruleType);
            }));
        }

        //then
        startLatch.countDown();

        List<Integer> sequenceList = futureList.stream()
                .map(future -> {
                    try {
                        return getSequence(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        assertThat(sequenceList).containsExactlyInAnyOrderElementsOf(
                IntStream.rangeClosed(1, threadCount).boxed().toList()
        );

        executorService.close();
    }
}
