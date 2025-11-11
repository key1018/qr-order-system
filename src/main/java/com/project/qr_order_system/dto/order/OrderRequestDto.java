package com.project.qr_order_system.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 신규 주문 생성 요청
 */
@Getter
@NoArgsConstructor
public class OrderRequestDto {
    private Long storeId;
    private Integer tableNumber; // takeout이면 null
    private Long paymentCardId; // 결제에 사용할 카드id
    private List<OrderItemRequestDto> orderItems;
}
