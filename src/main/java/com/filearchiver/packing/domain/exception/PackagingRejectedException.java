package com.filearchiver.packing.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class PackagingRejectedException extends RuntimeException {
    private static final String message = "올바르지 않은 Serial 포장 요청입니다.";

    public PackagingRejectedException(String nonexistentSerialList, String alreadyPackedSerial) {
        super(message);
        log.info("message = {}, nonexistentList  = {}, alreadyPackedList = {}", message, nonexistentSerialList, alreadyPackedSerial);
    }
}
