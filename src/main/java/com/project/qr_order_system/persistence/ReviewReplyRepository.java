package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.ReviewReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewReplyRepository extends JpaRepository<ReviewReplyEntity, Long> {
    // 중복 답변인지 확인
    boolean existsByReview_ReviewId(Long reviewId);

    // 매장 리뷰 전제 조회
    List<ReviewReplyEntity> findAllByReview_ReviewIdIn(List<Long> reviewIds);
}
