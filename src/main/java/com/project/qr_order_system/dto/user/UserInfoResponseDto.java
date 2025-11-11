package com.project.qr_order_system.dto.user;

import com.project.qr_order_system.dto.payment.PaymentCardResponseDto;
import com.project.qr_order_system.model.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.BindParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 내 정보 조회 응답
 */
@Getter
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String name;
    private Role role;
    private List<PaymentCardResponseDto> paymentCards; // 카드 목록

    @Builder
    public UserInfoResponseDto(Long id, String email, String name, Role role, List<PaymentCardResponseDto> paymentCards) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.role = role;
        this.paymentCards = new ArrayList<>();
    }
}
