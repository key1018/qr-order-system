package com.project.qr_order_system.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 통일된 API 성공 응답 래퍼 클래스
 * @param <T> 응답 데이터 타입
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;           // 성공 여부
    private String message;            // 응답 메시지
    private T data;                    // 응답 데이터 (제네릭)
    private LocalDateTime timestamp;    // 응답 시간
    private String code;                // 응답 코드 (선택사항)
}

