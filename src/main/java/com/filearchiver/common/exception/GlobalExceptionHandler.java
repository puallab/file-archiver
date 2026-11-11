package com.filearchiver.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(BusinessException ex, HttpServletRequest request){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                ex.getErrorCode().getStatus(),
                ex.getMessage()
        );

        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("errorCode", ex.getErrorCode().getCode());
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        if (!ex.getParams().isEmpty()) {
            problemDetail.setProperty("args", ex.getParams());
        }

        log.warn("{} : {} errorCode = {}, params = {}",
                ex.getClass().getSimpleName(), ex.getMessage(),
                ex.getErrorCode().getCode(), ex.getParams());

        return  problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleServerException(Exception ex, HttpServletRequest request){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                ErrorCode.INTERNAL_ERROR.getStatus(),
                "관리자에게 문의하세요."
        );

        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty("errorCode", ErrorCode.INTERNAL_ERROR.getCode());
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        log.error("Internal Server Error", ex);

        return problemDetail;
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {

        ProblemDetail problemDetail = ex.getBody();
        problemDetail.setProperty("errorCode", ErrorCode.INVALID_REQUEST.getCode());
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        Map<String, Object> invalidValues = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getRejectedValue() != null ? error.getRejectedValue() : "null",
                        (existing, replacement) -> existing
                ));

        problemDetail.setProperty("args", invalidValues);

        log.warn("{} : errorCode = {}, args = {}",
                ex.getClass().getSimpleName(), ErrorCode.INVALID_REQUEST.getCode(), invalidValues);

        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }
}
