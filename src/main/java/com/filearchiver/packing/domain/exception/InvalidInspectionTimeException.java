package com.filearchiver.packing.domain.exception;

import com.filearchiver.common.exception.BusinessException;
import com.filearchiver.common.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.Map;

public class InvalidInspectionTimeException extends BusinessException {
    private static final String message = "유효하지 않은 검사시각 입니다.";
    private static final ErrorCode errorCode = ErrorCode.INVALID_INSPECTION_TIME;
    private final LocalDateTime inspectionTime;
    private final LocalDateTime checkedTime;

    public InvalidInspectionTimeException(LocalDateTime inspectionTime, LocalDateTime checkedTime) {
        super(errorCode, message);
        this.inspectionTime = inspectionTime;
        this.checkedTime = checkedTime;
    }

    @Override
    public Map<String, Object> getParams() {
        return Map.of(
                "inspectionTime", this.inspectionTime,
                "checkedTime", this.checkedTime
        );
    }
}
