package com.project.qr_order_system.dto.order;

import com.project.qr_order_system.model.OrderStatus;
import org.hibernate.query.Order;

/**
 * 실시간 전송용 (주문 상황)
 */
public class OrderStatusUpdateDto {
    private Long orderId;
    private OrderStatus orderStatus;
    private Integer waitingPosition; // 내 앞의 대기 인원
    private Integer waitingTime; // 예상 대기 시간(분)
}
