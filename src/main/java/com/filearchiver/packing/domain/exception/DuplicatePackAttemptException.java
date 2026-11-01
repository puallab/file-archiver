package com.filearchiver.packing.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatePackAttemptException extends RuntimeException {
    private static final String message = "이미 포장된 Serial 입니다.";

    public DuplicatePackAttemptException() {
        super(message);
    }
}
