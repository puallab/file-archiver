package com.filearchiver.packing.domain.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.CONFLICT)
@Slf4j
public class DuplicatePackAttemptException extends RuntimeException {
    private static final String message = "이미 포장된 Serial 입니다.";

    public DuplicatePackAttemptException(String productList) {
        super(message);
        log.info("message = {}, list  = {}", message, productList);
    }
}
