package com.project.qr_order_system.dto.order;

import com.project.qr_order_system.model.OrderStatus;
import com.project.qr_order_system.model.StoreType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 완료/내역 조회 응답
 */
public class OrderResponseDto {
    private Long orderId;
    private StoreType storeType;
    private Integer tableNumber;
    private OrderStatus orderStatus;
    private Integer totalPrice;
    private LocalDateTime createAt;
    private String usedCardName;
    private List<OrderItemResponseDto> orderItems;
}
