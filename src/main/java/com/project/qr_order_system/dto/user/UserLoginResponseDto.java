package com.project.qr_order_system.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 응답
 */
@Getter
@AllArgsConstructor
public class UserLoginResponseDto {
    private String accessToken;
    private String refreshToken;
}
