package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.review.ReviewReplyRequestDto;
import com.project.qr_order_system.dto.review.ReviewResponseDto;
import com.project.qr_order_system.dto.review.ReviewCreateRequestDto;
import com.project.qr_order_system.model.*;
import com.project.qr_order_system.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRespository orderItemRespository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final ReviewReplyRepository reviewReplyRepository;

    // =================================================================
    // 고객 관련 비즈니스 로직
    // 리뷰 작성
    // =================================================================

    /**
     * 리뷰 작성 (고객용)
     * 상태 : DONE
     */
    @Transactional
    public ReviewResponseDto createReview(ReviewCreateRequestDto requestDto, String email){

        // 사용자 조회
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 주문 조회
        OrderEntity order = orderRepository.findById(requestDto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문 내역을 찾을 수 없습니다."));

        // 본인이 주문한 확인
        if(!order.getUser().getId().equals(user.getId())){
            new SecurityException("본인의 주문에만 리뷰 작성이 가능합니다.");
        }

        // 상태 확인 (상태 : DONE)
        if(!order.getStatus().equals(OrderStatus.DONE)){
            throw new IllegalArgumentException("메뉴 수령 완료 후 리뷰를 작성할 수 있습니다.");
        }

        // 중복 리뷰인지 확인
        if(reviewRepository.existsByOrderId(order.getId())){
            throw new IllegalArgumentException("이미 리뷰를 작성한 주문입니다.");
        }

         ReviewEntity review = ReviewEntity.builder()
                 .reviewContent(requestDto.getReviewContent())
                 .reviewImgUrl(requestDto.getReviewImageUrl())
                 .user(user)
                 .order(order)
                 .build();

        for(ReviewCreateRequestDto.ReviewItemRequestDto itemRequestDto : requestDto.getRatingList()){
                OrderItemEntity orderItem = orderItemRespository.findById(itemRequestDto.getOrderItemId())
                        .orElseThrow(() -> new IllegalArgumentException("주문 메뉴를 찾을 수 없습니다."));

                // 메뉴가 해당 주문 내역에 포함된 것인지 확인
                if(!orderItem.getOrder().getId().equals(order.getId())){
                    throw new SecurityException("주문 내역에 포함된 메뉴가 아닙니다.");
                }

                ReviewItemEntity reviewItem = ReviewItemEntity.builder()
                        .rating(itemRequestDto.getRating())
                        .orderItem(orderItem)
                        .build();

                review.addReviewItem(reviewItem);
        }

        ReviewEntity savedReview = reviewRepository.save(review);

        return getReviewResponseDto(savedReview, null);
    }

    // =================================================================
    // 사장님(관리자) 관련 비즈니스 로직
    // 리뷰 답변 작성
    // =================================================================

    /**
     * 리뷰 답변 작성 (관리자용)
     */
    @Transactional
    public ReviewResponseDto createReviewReply(Long reviewId, ReviewReplyRequestDto requestDto, String email){

        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰가 존재하지 않습니다."));

        UserEntity admin = review.getOrder().getStore().getOwner();

        if(!admin.getEmail().equals(email)){
            throw new SecurityException("해당 매장의 관리자가 아닙니다.");
        }

        // 답변이 이미 등롣된 리뷰인지 확인
        if(reviewReplyRepository.existsByReview_ReviewId(reviewId)){
            throw new IllegalArgumentException("이미 답글이 등록된 리뷰입니다.");
        }

        // 답글 저장
        ReviewReplyEntity reply = ReviewReplyEntity.builder()
                .replyContent(requestDto.getReviewReplyContent())
                .review(review)
                .user(admin)
                .build();

        reviewReplyRepository.save(reply);

        return getReviewResponseDto(review, reply);
    }

    // =================================================================
    // 공통 로직 분리
    // =================================================================

    private ReviewResponseDto getReviewResponseDto(ReviewEntity savedReview, ReviewReplyEntity savedReviewReply){
        List<ReviewResponseDto.ReviewItemInfo> reviewItemList = savedReview.getReviewItems()
                .stream()
                .map(itemDto -> ReviewResponseDto.ReviewItemInfo.builder()
                        .productName(itemDto.getOrderItem().getProduct().getProductName())
                        .rating(itemDto.getRating())
                        .build())
                .toList();

        return ReviewResponseDto.builder()
                .reviewId(savedReview.getReviewId())
                .reviewContent(savedReview.getReviewContent())
                .reviewImageUrl(savedReview.getReviewImgUrl())
                .reviewerName(savedReview.getUser().getName())
                .reviewDate(savedReview.getCreatedAt())
                .reviewItems(reviewItemList)
                .reviewReplyContent(savedReviewReply != null ? savedReviewReply.getReplyContent() : null)
                .replyDate(savedReviewReply != null ? savedReviewReply.getCreatedAt() : null)
                .build();
    }


}
