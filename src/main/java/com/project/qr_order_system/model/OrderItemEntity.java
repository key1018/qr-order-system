package com.project.qr_order_system.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orderItem")
public class OrderItemEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    private Integer quantity; // 고객이 주문한 수량
    private Integer totalprice; // 주문 당시 가격(총 가격)
}
