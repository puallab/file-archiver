package com.filearchiver.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DUPLICATED_PRODUCT_PACK(HttpStatus.CONFLICT, "PRODUCT_001"),
    DUPLICATED_SERIAL_REQUEST(HttpStatus.CONFLICT, "PRODUCT_002"),

    INVALID_INSPECTION_TIME(HttpStatus.BAD_REQUEST, "INSPECTION_001"),

    REJECT_PACKAGING_REQUEST(HttpStatus.CONFLICT, "PACKAGING_001"),

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR");

    private final HttpStatus status;
    private final String code;

    ErrorCode(HttpStatus httpStatus, String code) {
        this.status =  httpStatus;
        this.code = code;
    }
}
