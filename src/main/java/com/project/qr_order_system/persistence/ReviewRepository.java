package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.ReviewEntity;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    // 해당 주문의 리뷰가 이미 작성했는지 확인
    boolean existsByOrderId(Long orderId);

    // 가게 전체 리뷰 조회
    @Query("select r from ReviewEntity r " +
        "join fetch r.order o " +
        "join fetch r.user u " +
        "join fetch o.store s " +
        "where s.id = :storeId " +
        "order by r.createdAt desc"
    )
    Slice<ReviewEntity> findAllByOrder_Store_Id(@Param("storeId")Long storeId, Pageable pageable);

    // 내가 쓴 리뷰 조회
    @Query("select r from ReviewEntity r " +
      "join fetch r.order o " +
      "join fetch o.store s " +
      "where r.user.id = :userId " +
      "order by r.createdAt desc"
    )
    Slice<ReviewEntity> findAllByUser_Id(@Param("userId")Long userId, Pageable pageable);
}
