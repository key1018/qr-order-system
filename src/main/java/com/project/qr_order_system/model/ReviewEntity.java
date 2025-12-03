package com.project.qr_order_system.model;

import com.project.qr_order_system.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "review",
// [중복 방지] 한 주문 상품(order_item_id)에는 리뷰가 딱 1개만 달려야 함
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_review_order", // 유니크키
                columnNames = {"order_id"}
        )
        }
)
public class ReviewEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(nullable = false, length = 1000)
    private String reviewContent; // 리뷰내용

    private String reviewImgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // 작성자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewItemEntity> reviewItems = new ArrayList<>(); // 메뉴별 평점 리스트 (리뷰 저장 시 얘네도 같이 저장)

    @Builder
    public ReviewEntity(UserEntity user, OrderEntity order, String reviewContent, String reviewImgUrl) {
        this.user = user;
        this.order = order;
        this.reviewContent = reviewContent;
        this.reviewImgUrl = reviewImgUrl;
    }

    public void addReviewItem(ReviewItemEntity item) {
        this.reviewItems.add(item); // review 리스트에 자식 추가
        item.setReview(this); // 자식에게 부모 추가 (외래키값 설정)
    }
}
