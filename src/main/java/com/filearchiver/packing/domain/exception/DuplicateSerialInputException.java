package com.filearchiver.packing.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateSerialInputException extends RuntimeException {
    private static final String message = "요청 목록에 중복된 Serial이 포함되어 있습니다.";

    public DuplicateSerialInputException() {
        super(message);
    }
}
