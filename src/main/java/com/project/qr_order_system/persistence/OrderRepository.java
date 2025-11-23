package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.OrderEntity;
import com.project.qr_order_system.model.OrderStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    // 주문 목록 조회 (주문 상태별)
    List<OrderEntity> findByStoreIdAndStatus(Long storeId, OrderStatus status, Sort sort);
    // 주문 목록 조회 (전체)
    List<OrderEntity> findAllByStoreId(Long storeId, Sort sort);
}
