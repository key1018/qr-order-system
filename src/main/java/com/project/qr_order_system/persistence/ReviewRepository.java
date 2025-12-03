package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.ReviewEntity;
import com.querydsl.apt.jpa.JPAConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    // 해당 주문의 리뷰가 이미 작성했는지 확인
    boolean existsByOrderId(Long orderId);
}
