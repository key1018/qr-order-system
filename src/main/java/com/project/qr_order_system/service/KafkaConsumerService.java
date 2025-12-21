package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.kafka.EventType;
import com.project.qr_order_system.dto.kafka.OrderEvent;
import com.project.qr_order_system.dto.order.OrderStatusUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

/**
 * Kafka Consumer Service
 * 주문 이벤트를 Kafka 토픽(order-events)에서 수신하여 처리하는 서비스
 * 
 * 중요: Consumer Group ID는 각 서비스 개발자가 자신의 서비스에 맞게 설정해야 함
 * - 예: notification-service-group, analytics-service-group, inventory-service-group 등
 * - application.properties의 spring.kafka.consumer.group-id 값을 변경하면 됨
 * - 같은 group-id를 사용하는 Consumer들은 메시지를 공유하여 처리함
 * - 다른 group-id를 사용하는 Consumer들은 각각 독립적으로 메시지를 처리함
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    
    private final NotificationService notificationService;
    
    /**
     * 주문 이벤트 수신 및 처리
     * 
     * @param event 주문 이벤트 객체
     * @param acknowledgment 수동 Ack를 위한 객체 (로직 성공 시에만 acknowledge() 호출)
     * @param partition 파티션 번호
     * @param offset 오프셋
     */
    @KafkaListener(
            topics = "order-events", // 토픽 이름은 고정 (각 서비스 개발자가 이 토픽을 구독)
            // group-id는 application.properties에서 설정 (각 서비스 개발자가 자신의 서비스에 맞게 변경)
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderEvent(
            @Payload OrderEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("주문 이벤트 수신: orderId={}, eventType={}, partition={}, offset={}",
                event.getOrderId(),
                event.getEventType(),
                partition,
                offset);
        
        try {
            // 이벤트 타입에 따라 분기 처리
            switch (event.getEventType()) {
                case CREATE:
                    handleOrderCreated(event);
                    break;
                case UPDATE:
                    handleOrderUpdated(event);
                    break;
                case DELETE:
                    handleOrderDeleted(event);
                    break;
                default:
                    log.warn("알 수 없는 이벤트 타입: {}", event.getEventType());
            }
            
            // 처리 성공 시 수동 Ack 전송
            acknowledgment.acknowledge();
            log.info("주문 이벤트 처리 완료 및 Ack 전송: orderId={}, eventType={}",
                    event.getOrderId(),
                    event.getEventType());
            
        } catch (Exception e) {
            log.error("주문 이벤트 처리 실패: orderId={}, eventType={}",
                    event.getOrderId(),
                    event.getEventType(),
                    e);
            // 에러 발생 시 Ack를 보내지 않음 -> 재시도됨
            // 필요시 Dead Letter Queue로 전송하거나 재시도 로직 추가 가능
            throw e; // 예외를 던져서 재시도 트리거
        }
    }
    
    /**
     * 주문 생성 이벤트 처리
     */
    private void handleOrderCreated(OrderEvent event) {
        log.info("주문 생성 이벤트 처리 시작: orderId={}, storeId={}, userId={}",
                event.getOrderId(),
                event.getStoreId(),
                event.getUserId());
        
        // 관리자에게 주문 알림 전송
        // TODO: OrderResponseDto로 변환하여 알림 전송
        // notificationService.sendOrderAlert(event.getStoreId(), "new-order", orderResponseDto);
        
        log.info("주문 생성 이벤트 처리 완료: orderId={}", event.getOrderId());
    }
    
    /**
     * 주문 상태 변경 이벤트 처리
     */
    private void handleOrderUpdated(OrderEvent event) {
        log.info("주문 상태 변경 이벤트 처리 시작: orderId={}, previousStatus={}, newStatus={}",
                event.getOrderId(),
                event.getPreviousStatus(),
                event.getNewStatus());
        
        // 고객에게 주문 상태 변경 알림 전송
        OrderStatusUpdateDto updateDto = OrderStatusUpdateDto.builder()
                .orderId(event.getOrderId())
                .orderStatus(event.getNewStatus())
                .waitingPosition(event.getWaitingPosition())
                .waitingTime(event.getWaitingTime())
                .cancelReason(event.getCancelReason())
                .build();
        
        notificationService.sendCustomerOrderAlert(event.getOrderId(), updateDto);
        
        // 재고 복구 처리 (거절/취소 시)
        if (event.getRestoreStock() != null && event.getRestoreStock() && event.getOrderItems() != null) {
            log.info("재고 복구 처리: orderId={}", event.getOrderId());
            // TODO: 재고 복구 로직 구현
            // inventoryService.restoreStock(event.getOrderItems());
        }
        
        log.info("주문 상태 변경 이벤트 처리 완료: orderId={}", event.getOrderId());
    }
    
    /**
     * 주문 삭제(취소) 이벤트 처리
     */
    private void handleOrderDeleted(OrderEvent event) {
        log.info("주문 삭제 이벤트 처리 시작: orderId={}, cancelReason={}",
                event.getOrderId(),
                event.getCancelReason());
        
        // 고객에게 취소 알림 전송
        OrderStatusUpdateDto updateDto = OrderStatusUpdateDto.builder()
                .orderId(event.getOrderId())
                .orderStatus(event.getNewStatus())
                .cancelReason(event.getCancelReason())
                .waitingPosition(0)
                .waitingTime(0)
                .build();
        
        notificationService.sendCustomerOrderAlert(event.getOrderId(), updateDto);
        
        // 재고 복구 처리
        if (event.getRestoreStock() != null && event.getRestoreStock() && event.getOrderItems() != null) {
            log.info("재고 복구 처리: orderId={}", event.getOrderId());
            // TODO: 재고 복구 로직 구현
            // inventoryService.restoreStock(event.getOrderItems());
        }
        
        log.info("주문 삭제 이벤트 처리 완료: orderId={}", event.getOrderId());
    }
}

