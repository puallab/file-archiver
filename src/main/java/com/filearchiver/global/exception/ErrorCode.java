package com.filearchiver.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_INSPECTION_TIME(HttpStatus.BAD_REQUEST, "ISP01", "올바르지 않은 검사시각입니다"),

    DUPLICATE_SERIAL_INPUT(HttpStatus.CONFLICT, "PACK01", "요청 목록에 중복된 Serial이 포함되어 있습니다."),
    DUPLICATE_PACK_ATTEMPT(HttpStatus.CONFLICT, "PACK02", "이미 포장된 Serial 입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
