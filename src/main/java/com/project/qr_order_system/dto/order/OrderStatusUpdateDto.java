package com.project.qr_order_system.dto.order;

import com.project.qr_order_system.model.OrderStatus;
import lombok.*;
import org.hibernate.query.Order;

/**
 * 실시간 전송용 (주문 상황)
 */
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateDto {
    private Long orderId;
    private OrderStatus orderStatus;
    private Integer waitingPosition; // 내 앞의 대기 인원
    private Integer waitingTime; // 예상 대기 시간(분)
    private String cancelReason;
    private String message;
}
