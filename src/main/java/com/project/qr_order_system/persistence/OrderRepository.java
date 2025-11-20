package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
