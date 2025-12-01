package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.order.OrderResponseDto;
import com.project.qr_order_system.dto.order.OrderStatusUpdateDto;
import com.project.qr_order_system.model.OrderEntity;
import com.project.qr_order_system.model.OrderStatus;
import com.project.qr_order_system.persistence.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    /**
     * 주문 완료 처리 (관리자용)
     * 자동처리 (READY 상태가 10분 이상인 경우)
     * 주기 : 1분마다 실행
     * 상태 : READY -> DONE
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void autoFinishOrder() {

        // 기준 시간 설정 : 현재 시간 - 10분
        // 즉, READY 상태에서 updateAt 시간이 10분 넘게 방치
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(1);

        log.info("스케줄러 실행: {} 이전에 READY 된 주문을 처리합니다.", cutoffTime);

        // 미처리 고객 조회
        List< OrderEntity> orderList = orderRepository.findAllByStatusAndUpdatedAtBefore(OrderStatus.READY, cutoffTime);

        if(orderList.isEmpty()) {
            return;
        }

        int updatedCount = 0;

        for(OrderEntity order : orderList) {

            order.setStatus(OrderStatus.DONE); // DONE으로 변경
            order.setUpdatedBy("FINISH_SYSTEM");

            OrderStatusUpdateDto orderStatusUpdateDto = OrderStatusUpdateDto.builder()
                    .orderId(order.getId())
                    .orderStatus(order.getStatus())
                    .waitingTime(0)
                    .waitingPosition(0)
                    .message("주문이 완료되었습니다. 맛있게 드세요! ⭐️ 리뷰 작성은 큰 힘이 됩니다.")
                    .build();

            notificationService.sendCustomerOrderAlert(order.getId(), orderStatusUpdateDto);

            updatedCount++;
        }

        if(updatedCount > 0) {
            log.info("[스케줄러] 10분 경과된 대기 주문 {}건을 자동 완료(DONE) 처리했습니다.", updatedCount);
        }
    }
}
