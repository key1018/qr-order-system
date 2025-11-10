package com.project.qr_order_system.dto.payment;

/**
 * 카드등록 요청
 */
public class PaymentCardRegisterRequestDto {
    private String cardToken;
    private String cardName;
    private boolean isDefault;
}
