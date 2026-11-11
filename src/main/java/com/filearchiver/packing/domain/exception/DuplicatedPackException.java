package com.filearchiver.packing.domain.exception;

import com.filearchiver.common.exception.BusinessException;
import com.filearchiver.common.exception.ErrorCode;

import java.util.Map;

public class DuplicatedPackException extends BusinessException {
    private static final String message = "이미 포장된 제품입니다.";
    private static final ErrorCode errorCode = ErrorCode.DUPLICATED_PRODUCT_PACK;
    private final String serial;

    public DuplicatedPackException(String serial) {
        super(errorCode, message);
        this.serial = serial;
    }

    @Override
    public Map<String, Object> getParams() {
        return Map.of("serial", this.serial);
    }
}
