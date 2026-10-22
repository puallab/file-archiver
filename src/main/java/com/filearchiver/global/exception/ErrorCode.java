package com.filearchiver.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INSPECTION_TIME(HttpStatus.BAD_REQUEST, "ISP01", "올바르지 않은 검사시각입니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
