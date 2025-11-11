package com.project.qr_order_system.dto.order;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 요청 내부
 */
@Getter
@NoArgsConstructor
public class OrderItemRequestDto {
    private Long productId;
    private Integer quantity;
}
