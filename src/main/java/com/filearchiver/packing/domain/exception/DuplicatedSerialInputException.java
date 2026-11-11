package com.filearchiver.packing.domain.exception;

import com.filearchiver.common.exception.BusinessException;
import com.filearchiver.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;

public class DuplicatedSerialInputException extends BusinessException {
    private static final String message = "요청 목록에 중복된 Serial이 포함되어 있습니다.";
    private static final ErrorCode errorCode = ErrorCode.DUPLICATED_SERIAL_REQUEST;
    private final List<String> serials;

    public DuplicatedSerialInputException(List<String> serials) {
        super(errorCode, message);
        this.serials = serials;
    }

    @Override
    public Map<String, Object> getParams() {
        return Map.of("serials", serials);
    }
}
