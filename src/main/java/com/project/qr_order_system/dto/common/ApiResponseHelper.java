package com.project.qr_order_system.dto.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * API 응답 생성 헬퍼 클래스
 * Controller에서 일관된 응답 형식을 쉽게 생성할 수 있도록 도와주는 유틸리티
 */
public class ApiResponseHelper {
    
    /**
     * 성공 응답 생성 (데이터 포함)
     * @param data 응답 데이터
     * @param message 응답 메시지
     * @return ApiResponse 래핑된 ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.ok(response);
    }
    
    /**
     * 성공 응답 생성 (데이터 없음)
     * @param message 응답 메시지
     * @return ApiResponse 래핑된 ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return success(null, message);
    }
    
    /**
     * 성공 응답 생성 (기본 메시지)
     * @param data 응답 데이터
     * @return ApiResponse 래핑된 ResponseEntity
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(data, "요청이 성공적으로 처리되었습니다");
    }
    
    /**
     * 에러 응답 생성
     * @param message 에러 메시지
     * @param code 에러 코드
     * @param status HTTP 상태 코드
     * @return ApiErrorResponse 래핑된 ResponseEntity
     */
    public static ResponseEntity<ApiErrorResponse> error(
            String message, 
            String code, 
            HttpStatus status) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * 에러 응답 생성 (상세 에러 목록 포함)
     * @param message 에러 메시지
     * @param code 에러 코드
     * @param errors 상세 에러 목록
     * @param status HTTP 상태 코드
     * @return ApiErrorResponse 래핑된 ResponseEntity
     */
    public static ResponseEntity<ApiErrorResponse> error(
            String message, 
            String code,
            List<String> errors,
            HttpStatus status) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .success(false)
                .message(message)
                .code(code)
                .timestamp(LocalDateTime.now())
                .errors(errors)
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }
}

