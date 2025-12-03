package com.project.qr_order_system.dto.order;

import lombok.Builder;
import lombok.Getter;

/**
 * 주문 응답 내부
 */
@Getter
public class OrderItemResponseDto {
    private long productId;
    private String productName;
    private Integer quantity;
    private Integer orderPrice;

    @Builder
    public OrderItemResponseDto(long productId, String productName, Integer quantity, Integer orderPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.orderPrice = orderPrice;
    }
}
