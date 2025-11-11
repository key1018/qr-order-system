package com.project.qr_order_system.dto.payment;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카드등록 요청
 */
@Getter
@NoArgsConstructor
public class PaymentCardRegisterRequestDto {
    private String cardToken;
    private String cardName;
    private boolean isDefault;
}
