package com.project.qr_order_system.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 통일된 API 요청 래퍼 클래스
 * @param <T> 요청 데이터 타입
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequest<T> {
    private T data;                    // 요청 데이터 (제네릭)
    private LocalDateTime timestamp;    // 요청 시간 (선택사항)
}



