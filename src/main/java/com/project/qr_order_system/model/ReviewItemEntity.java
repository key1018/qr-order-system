package com.project.qr_order_system.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "review_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewItemEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewItemId;

    @Column(nullable = false)
    private Integer rating; // 이 메뉴에 대한 점수 (1~5)

    // 어떤 메뉴에 대한 점수인지ORDERS
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItemEntity orderItem;

    // 어느 리뷰에 속해있는지
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;

    @Builder
    public ReviewItemEntity(Integer rating, OrderItemEntity orderItem) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("별점은 1~5점 사이여야 합니다.");
        }
        this.rating = rating;
        this.orderItem = orderItem;
    }
}