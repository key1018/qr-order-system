package com.project.qr_order_system.model;

import com.project.qr_order_system.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "review_reply")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewReplyEntity  extends BaseEntity {

    @Id @GeneratedValue
    private Long reviewReplyId;

    @Column(nullable = false)
    private String replyContent;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review; // 어떤 리뷰에 댓글을 다는 것인지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private UserEntity user; // 사장님

    @Builder
    public ReviewReplyEntity(String replyContent, ReviewEntity review, UserEntity user) {
        this.replyContent = replyContent;
        this.review = review;
        this.user = user;
    }
}
