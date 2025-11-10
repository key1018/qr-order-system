package com.project.qr_order_system.dto.user;

import com.project.qr_order_system.dto.payment.PaymentCardResponseDto;
import com.project.qr_order_system.model.Role;

import java.util.List;

/**
 * 내 정보 조회 응답
 */
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private List<PaymentCardResponseDto> paymentCards; // 카드 목록
}
