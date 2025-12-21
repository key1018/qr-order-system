package com.project.qr_order_system.controller.advice;

import com.project.qr_order_system.dto.common.ApiErrorResponse;
import com.project.qr_order_system.dto.common.ApiResponseHelper;
import com.project.qr_order_system.exception.OutOfStockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 재고 부족 안내
    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ApiErrorResponse> handleOutOfStockException(OutOfStockException e) {
        return ApiResponseHelper.error(
                e.getMessage(),
                "OUT_OF_STOCK",
                HttpStatus.CONFLICT
        );
    }

    // 잘못된 인자 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponseHelper.error(
                e.getMessage(),
                "INVALID_INPUT",
                HttpStatus.BAD_REQUEST
        );
    }

    // 권한 없음 예외 처리
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiErrorResponse> handleSecurityException(SecurityException e) {
        return ApiResponseHelper.error(
                e.getMessage(),
                "FORBIDDEN",
                HttpStatus.FORBIDDEN
        );
    }

}
