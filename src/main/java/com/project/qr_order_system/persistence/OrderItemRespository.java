package com.project.qr_order_system.persistence;


import com.project.qr_order_system.model.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRespository extends JpaRepository<OrderItemEntity, Long> {
}
