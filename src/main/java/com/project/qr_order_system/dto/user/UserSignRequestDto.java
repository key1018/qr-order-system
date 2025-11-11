package com.project.qr_order_system.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청
 */
@Getter
@NoArgsConstructor
public class UserSignRequestDto {
    private String email;
    private String password;
    private String name;
}
