package com.filearchiver.packing.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Slf4j
public class InvalidInspectionTimeException extends RuntimeException {
    private static final String message = "유효하지 않은 검사시각 입니다.";

    public InvalidInspectionTimeException(LocalDateTime inspectionTime, LocalDateTime checkedTime){
        super(message);
        log.info("message = {}, inspectionTime  = {}, occurredTime = {}", message, inspectionTime, checkedTime);
    }
}
