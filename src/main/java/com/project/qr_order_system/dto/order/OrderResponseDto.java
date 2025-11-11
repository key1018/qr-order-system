package com.project.qr_order_system.dto.order;

import com.project.qr_order_system.model.OrderStatus;
import com.project.qr_order_system.model.StoreType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 완료/내역 조회 응답
 */
@Getter
public class OrderResponseDto {
    private Long orderId;
    private StoreType storeType;
    private Integer tableNumber;
    private OrderStatus orderStatus;
    private Integer totalPrice;
    private LocalDateTime createAt;
    private String usedCardName;
    private List<OrderItemResponseDto> orderItems;

    @Builder
    public OrderResponseDto(Long orderId, StoreType storeType, Integer tableNumber, OrderStatus orderStatus,
                            Integer totalPrice, LocalDateTime createAt, String usedCardName, List<OrderItemResponseDto> orderItems) {
        this.orderId = orderId;
        this.storeType = storeType;
        this.tableNumber = tableNumber;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.createAt = createAt;
        this.usedCardName = usedCardName;
        this.orderItems = orderItems;
    }
}
