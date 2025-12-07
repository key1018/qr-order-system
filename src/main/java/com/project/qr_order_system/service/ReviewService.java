package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.review.ReviewReplyRequestDto;
import com.project.qr_order_system.dto.review.ReviewResponseDto;
import com.project.qr_order_system.dto.review.ReviewCreateRequestDto;
import com.project.qr_order_system.model.*;
import com.project.qr_order_system.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 내가 쓴 리뷰 전체 조회(고객용)
     */
    @Transactional(readOnly = true)
    public Slice<ReviewResponseDto> getMyReviews(String email, Pageable pageable){

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Slice<ReviewEntity> reviews = reviewRepository.findAllByUser_Id(user.getId(), pageable);

        // 리뷰없으면 빈 리스트로 처리
        if(reviews.isEmpty()){
            return reviews.map(review -> null);
        }

        // reviews : Slice 자체
        // getContent() : List<ReviewEntity> 데이터 리스트
        // stream : List<ReviewEntity> 데이터 리스트 하나씩 처리해서 reviewId 꺼냄
        // => 값 : [1,2]
        List<Long> reviewIds = reviews.getContent().stream()
                .map(ReviewEntity::getReviewId).toList();

        // [1,2]넣고 List<ReviewReplyEntity>로 한꺼번에 조회됨
        List<ReviewReplyEntity> reply = reviewReplyRepository.findAllByReview_ReviewIdIn(reviewIds);

        // key : 리뷰id, value = ReviewReplyEntity
        // List<ReviewReplyEntity>에 있는 ReviewReplyEntity를 stream으로 하나씩 뺸 뒤에
        // ReviewReplyEntity를 r로 정의한 뒤 r.getReview().getReviewId()해서 key
        // r -> r자체로 해서 value 값을 꺼냄
        // 값 : {
        //  1 : 1번_리뷰의_답글_객체(Entity),
        //  2 : 2번_리뷰의_답글_객체(Entity)
        //}
        Map<Long, ReviewReplyEntity> replyMap = reply.stream()
                .collect(Collectors.toMap(r -> r.getReview().getReviewId() // key
                        , r -> r) // value (ReviewReplyEntity 객체)
                );

        // reviews 자체는 ReviewEntity들이 담겨 있는 박스(Slice)
        // map을 통해 ReviewEntity들을 하나씩 빼냄
        // 뺴낸 값을 review라고 정의한 뒤 ReviewReplyEntity에 reviewId값을 넣어서 ReviewReplyEntity 값 빼냄
        // getReviewResponseDto(review, replyEntity)에 reviewEntity값, ReviewReplyEntity값을 넣어서 최종 return
        return reviews.map(review -> { // review : ReviewEntity
            ReviewReplyEntity replyEntity = replyMap.get(review.getReviewId());
            return getReviewResponseDto(review, replyEntity);
        });
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
    // 고객, 사장님(관리자) 관련 비즈니스 로직
    // 가게 리뷰 조회
    // =================================================================


    @Transactional(readOnly = true)
    public Slice<ReviewResponseDto> getStoreReviews(Long storeId, Pageable pageable){

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을  수 없습니다."));

        Slice<ReviewEntity> reviews = reviewRepository.findAllByOrder_Store_Id(store.getId(), pageable);

        if(reviews.isEmpty()){
            return reviews.map(review -> null);
        }

        List<Long> reviewId = reviews.getContent().stream()
                .map(ReviewEntity::getReviewId).toList();

        List<ReviewReplyEntity> reply = reviewReplyRepository.findAllByReview_ReviewIdIn(reviewId);

        Map<Long, ReviewReplyEntity> replyMap = reply.stream()
                .collect(Collectors.toMap(r -> r.getReview().getReviewId()
                , r -> r));

        return reviews.map(review -> {
            ReviewReplyEntity reviewReply = replyMap.get(review.getReviewId());
            return getReviewResponseDto(review, reviewReply);
        });
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
                .storeId(savedReview.getOrder().getStore().getId())
                .storeName(savedReview.getOrder().getStore().getStoreName())
                .storeImage(savedReview.getOrder().getStore().getStoreImage())
                .build();
    }
}
