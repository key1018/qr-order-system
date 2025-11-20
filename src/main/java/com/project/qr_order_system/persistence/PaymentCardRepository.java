package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.PaymentCardEntity;
import com.project.qr_order_system.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCardRepository extends JpaRepository<PaymentCardEntity, Long> {
}
