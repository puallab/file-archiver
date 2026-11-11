package com.filearchiver.packing.domain.exception;

import com.filearchiver.common.exception.BusinessException;
import com.filearchiver.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;

public class PackagingRejectedException extends BusinessException {
    private static final String message = "올바르지 않은 Serial 포장 요청입니다.";
    private static final ErrorCode errorCode = ErrorCode.REJECT_PACKAGING_REQUEST;
    private final List<String> nonexistentSerials;
    private final List<String> alreadyPackedSerials;

    public PackagingRejectedException(List<String> nonexistentSerials, List<String> alreadyPackedSerials) {
        super(errorCode, message);
        this.nonexistentSerials = nonexistentSerials;
        this.alreadyPackedSerials = alreadyPackedSerials;
    }

    @Override
    public Map<String, Object> getParams() {
        return Map.of(
                "nonexistentSerials", this.nonexistentSerials,
                "alreadyPackedSerials", this.alreadyPackedSerials
        );
    }
}
