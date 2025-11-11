package com.project.qr_order_system.dto.payment;

import lombok.Builder;
import lombok.Getter;

/**
 * 카드등록 응답
 */
@Getter
public class PaymentCardResponseDto {
    private Long id;
    private String cardName;
    private boolean isDefault;

    @Builder
    public PaymentCardResponseDto(Long id, String cardName, boolean isDefault) {
        this.id = id;
        this.cardName = cardName;
        this.isDefault = isDefault;
    }
}
