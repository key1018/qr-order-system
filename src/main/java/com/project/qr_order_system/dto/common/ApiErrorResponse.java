package com.project.qr_order_system.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 통일된 API 에러 응답 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private boolean success;           // 항상 false
    private String message;            // 에러 메시지
    private String code;               // 에러 코드
    private LocalDateTime timestamp;    // 에러 발생 시간
    private List<String> errors;       // 상세 에러 목록 (선택사항)
}

