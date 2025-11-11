package com.project.qr_order_system.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * 로그인 요청
 */
@Getter
@NoArgsConstructor
public class UserLoginRequestDto {
    private String email;
    private String password;
}
