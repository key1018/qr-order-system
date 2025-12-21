package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.kafka.EventType;
import com.project.qr_order_system.dto.kafka.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer Service
 * 주문 이벤트를 Kafka 토픽(order-events)에 발행하는 서비스
 * 
 * 중요: orderId를 Key로 사용하여 같은 주문의 이벤트는 같은 파티션으로 전송됨 (순서 보장)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {
    
    /**
     * Kafka 토픽 이름 (고정)
     * 각 서비스 개발자는 이 토픽 이름을 사용하여 이벤트를 발행/수신함
     */
    private static final String TOPIC_NAME = "order-events";
    
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    /**
     * 주문 생성 이벤트 발행
     * 
     * @param orderId 주문 ID (Kafka Key로 사용 - 순서 보장)
     * @param storeId 매장 ID
     * @param userId 사용자 ID
     * @param totalPrice 총 주문 금액
     * @param orderItems 주문 상세 항목 리스트
     */
    public void sendOrderCreatedEvent(
            Long orderId,
            Long storeId,
            Long userId,
            Integer totalPrice,
            java.util.List<OrderEvent.OrderItemEvent> orderItems
    ) {
        OrderEvent event = OrderEvent.builder()
                .eventType(EventType.CREATE)
                .orderId(orderId)
                .storeId(storeId)
                .userId(userId)
                .newStatus(com.project.qr_order_system.model.OrderStatus.ORDERED)
                .totalPrice(totalPrice)
                .orderItems(orderItems)
                .timestamp(java.time.LocalDateTime.now())
                .build();
        
        sendEvent(orderId, event);
    }
    
    /**
     * 주문 상태 변경 이벤트 발행
     * 
     * @param orderId 주문 ID (Kafka Key로 사용 - 순서 보장)
     * @param storeId 매장 ID
     * @param userId 사용자 ID
     * @param previousStatus 이전 주문 상태
     * @param newStatus 새로운 주문 상태
     * @param waitingPosition 대기 순위
     * @param waitingTime 예상 대기 시간(분)
     * @param cancelReason 취소/거절 사유
     * @param restoreStock 재고 복구 여부
     * @param updatedBy 수정자
     */
    public void sendOrderUpdatedEvent(
            Long orderId,
            Long storeId,
            Long userId,
            com.project.qr_order_system.model.OrderStatus previousStatus,
            com.project.qr_order_system.model.OrderStatus newStatus,
            Integer waitingPosition,
            Integer waitingTime,
            String cancelReason,
            Boolean restoreStock,
            String updatedBy
    ) {
        OrderEvent event = OrderEvent.builder()
                .eventType(EventType.UPDATE)
                .orderId(orderId)
                .storeId(storeId)
                .userId(userId)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .waitingPosition(waitingPosition)
                .waitingTime(waitingTime)
                .cancelReason(cancelReason)
                .restoreStock(restoreStock)
                .timestamp(java.time.LocalDateTime.now())
                .updatedBy(updatedBy)
                .build();
        
        sendEvent(orderId, event);
    }
    
    /**
     * 주문 삭제(취소) 이벤트 발행
     * 
     * @param orderId 주문 ID (Kafka Key로 사용 - 순서 보장)
     * @param storeId 매장 ID
     * @param userId 사용자 ID
     * @param previousStatus 이전 주문 상태
     * @param cancelReason 취소 사유
     * @param restoreStock 재고 복구 여부
     * @param orderItems 주문 상세 항목 리스트 (재고 복구 시 필요)
     * @param updatedBy 수정자
     */
    public void sendOrderDeletedEvent(
            Long orderId,
            Long storeId,
            Long userId,
            com.project.qr_order_system.model.OrderStatus previousStatus,
            String cancelReason,
            Boolean restoreStock,
            java.util.List<OrderEvent.OrderItemEvent> orderItems,
            String updatedBy
    ) {
        OrderEvent event = OrderEvent.builder()
                .eventType(EventType.DELETE)
                .orderId(orderId)
                .storeId(storeId)
                .userId(userId)
                .previousStatus(previousStatus)
                .newStatus(com.project.qr_order_system.model.OrderStatus.CANCELED)
                .cancelReason(cancelReason)
                .restoreStock(restoreStock)
                .orderItems(orderItems)
                .timestamp(java.time.LocalDateTime.now())
                .updatedBy(updatedBy)
                .build();
        
        sendEvent(orderId, event);
    }
    
    /**
     * Kafka에 이벤트 전송 (공통 메서드)
     * orderId를 Key로 사용하여 같은 주문의 이벤트는 같은 파티션으로 전송됨 (순서 보장)
     * 
     * @param orderId 주문 ID (Kafka Key)
     * @param event 주문 이벤트 객체
     */
    private void sendEvent(Long orderId, OrderEvent event) {
        try {
            // orderId를 Key로 사용하여 순서 보장
            String key = String.valueOf(orderId);
            
            CompletableFuture<SendResult<String, OrderEvent>> future = 
                    kafkaTemplate.send(TOPIC_NAME, key, event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("주문 이벤트 발행 성공: orderId={}, eventType={}, partition={}, offset={}",
                            orderId,
                            event.getEventType(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("주문 이벤트 발행 실패: orderId={}, eventType={}",
                            orderId,
                            event.getEventType(),
                            ex);
                    // 실패해도 주문은 이미 저장되었으므로 예외를 던지지 않음
                    // 필요시 Dead Letter Queue나 재시도 로직 추가 가능
                }
            });
            
        } catch (Exception e) {
            log.error("주문 이벤트 발행 중 예외 발생: orderId={}, eventType={}",
                    orderId,
                    event.getEventType(),
                    e);
        }
    }
}

