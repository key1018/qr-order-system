package com.project.qr_order_system.dto.kafka;

import com.project.qr_order_system.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 이벤트 DTO
 * CREATE, UPDATE, DELETE 모든 이벤트를 단일 토픽(order-events)에서 처리하기 위한 통합 이벤트 객체
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    
    /**
     * 이벤트 타입 (CREATE, UPDATE, DELETE)
     */
    private EventType eventType;
    
    /**
     * 주문 ID (Kafka Key로 사용됨 - 순서 보장을 위해)
     */
    private Long orderId;
    
    /**
     * 매장 ID
     */
    private Long storeId;
    
    /**
     * 사용자 ID
     */
    private Long userId;
    
    /**
     * 이전 주문 상태 (UPDATE, DELETE 시 사용)
     */
    private OrderStatus previousStatus;
    
    /**
     * 새로운 주문 상태 (CREATE, UPDATE 시 사용)
     */
    private OrderStatus newStatus;
    
    /**
     * 총 주문 금액
     */
    private Integer totalPrice;
    
    /**
     * 주문 상세 항목 리스트
     */
    private List<OrderItemEvent> orderItems;
    
    /**
     * 취소/거절 사유 (UPDATE, DELETE 시 사용)
     */
    private String cancelReason;
    
    /**
     * 대기 순위 (UPDATE 시 사용)
     */
    private Integer waitingPosition;
    
    /**
     * 예상 대기 시간(분) (UPDATE 시 사용)
     */
    private Integer waitingTime;
    
    /**
     * 재고 복구 여부 (UPDATE, DELETE 시 사용)
     */
    private Boolean restoreStock;
    
    /**
     * 이벤트 발생 시간
     */
    private LocalDateTime timestamp;
    
    /**
     * 수정자 (이메일)
     */
    private String updatedBy;
    
    /**
     * 주문 상세 항목 이벤트 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemEvent {
        private Long productId;
        private String productName;
        private Integer quantity;
        private Integer orderPrice;
    }
}

