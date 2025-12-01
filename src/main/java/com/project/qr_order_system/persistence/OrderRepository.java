package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.OrderEntity;
import com.project.qr_order_system.model.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // 주문 목록 조회 (주문 상태별)
    List<OrderEntity> findByStoreIdAndStatus(Long storeId, OrderStatus status, Sort sort); // 관리자
    List<OrderEntity> findByUserIdAndStatus(Long userId, OrderStatus status, Sort sort);
    // 주문 목록 조회 (전체)
    List<OrderEntity> findAllByStoreId(Long storeId, Sort sort); // 관리자
    List<OrderEntity> findAllByUserId(Long userId, Sort sort);
    // 내가 주문한 가게에서 대기하는 사람 인원(status : Progress)
    // 내 orderId가 10이라고 가정했을 경우
    // ex) select count(0) from OrderEntity where storeId = 1 and status = 'IN_PROGRESS' and orderId < 10;
    long countByStoreIdAndStatusAndIdLessThan(Long storeId, OrderStatus status, Long orderId);

    // [스케줄러용] 주문 자동 완료(DONE) 미처리 고객 조회
    // 조건 : 상태가 READY 이고, 수정된 시간(updateAt)이 '10분 전(cutoffTime)'보다 과거인 경우
    // 동작 : 상태를 DONE 으로 변경, 수정한 사람(updateBy)를 'FINISH_SYSTEM'으로 기록
    List<OrderEntity> findAllByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime cutoffTime);
}
