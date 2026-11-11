package com.filearchiver.packing.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class DuplicatedPackException extends RuntimeException{
    private static final String message = "이미 포장된 제품입니다.";

    public DuplicatedPackException(String serial) {
        super(message);
        log.info("message = {}, serial  = {}", message, serial);
    }
}
