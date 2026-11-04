package com.filearchiver.packing.application;

import com.filearchiver.TestcontainersConfiguration;
import com.filearchiver.packing.infrastructure.persistence.InspectionFileRepository;
import com.filearchiver.packing.infrastructure.persistence.ProductRepository;
import com.filearchiver.packing.presentation.dto.InspectionFileDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class InspectionFileServiceIntegrationTest {
    @Autowired
    private InspectionFileService inspectionFileService;

    @Autowired
    private InspectionFileRepository inspectionFileRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void deleteAllData(){
        inspectionFileRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void 같은_제품_다른_검사시각_등록시_제품은_1개_검사파일은_각각_생성(){
        //given
        LocalDateTime inspectedAt = LocalDateTime.now();

        Long firstFileId = inspectionFileService.register(
                new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt.minusMinutes(2),
                        "SENSOR-01",
                        "EQ-07",
                        "inspection-files/SN-0001.txt"
                )
        );

        //when
        Long secondFileId = inspectionFileService.register(
                new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt.minusMinutes(1),
                        "SENSOR-02",
                        "EQ-08",
                        "inspection-files/SN-0002.txt"
                )
        );

        //then
        assertThat(firstFileId).isNotEqualTo(secondFileId);
        assertThat(inspectionFileRepository.count()).isEqualTo(2L);

        //제품은 1개만 생성된다
        assertThat(productRepository.count()).isEqualTo(1L);
    }

    @Test
    void 동시에_신규_생성시_유니크_무결성_예외_발생한다(){
        //given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        LocalDateTime inspectedAt = LocalDateTime.now();

        //when
        List<Future<Long>> futureList = new ArrayList<>();
        for(int i =0; i<threadCount; i++){
            futureList.add(executorService.submit(() -> {
                startLatch.await();
                return inspectionFileService.register(new InspectionFileDto.RegisterRequest(
                        "SN-0001",
                        inspectedAt,
                        "SENSOR-01",
                        "EQ-07",
                        "inspection-files/SN-0001.txt"
                ));
            }));
        }

        startLatch.countDown();

        //then
        int successCount = 0;
        int failCount =0;
        for (Future<Long> future : futureList) {
            try {
                future.get();
                successCount += 1;
            } catch (InterruptedException | ExecutionException e) {
                if(e.getCause() instanceof DataIntegrityViolationException){
                    failCount += 1;
                }
            }
        }

        assertThat(successCount).isEqualTo(1);
        assertThat(failCount).isEqualTo(threadCount-1);
        assertThat(inspectionFileRepository.count()).isEqualTo(1);
        assertThat(productRepository.count()).isEqualTo(1);

        executorService.close();
    }
}
