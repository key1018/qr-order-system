package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.common.ApiRequest;
import com.project.qr_order_system.dto.common.ApiResponse;
import com.project.qr_order_system.dto.common.ApiResponseHelper;
import com.project.qr_order_system.dto.review.ReviewReplyRequestDto;
import com.project.qr_order_system.dto.review.ReviewResponseDto;
import com.project.qr_order_system.dto.review.ReviewCreateRequestDto;
import com.project.qr_order_system.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qrorder")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/users/reviews/createreviews")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(@Valid @RequestBody ApiRequest<ReviewCreateRequestDto> request, Principal principal) {
        ReviewResponseDto responseDto = reviewService.createReview(request.getData(), principal.getName());
        return ApiResponseHelper.success(responseDto, "리뷰가 성공적으로 작성되었습니다");
    }

    @GetMapping("/users/reviews/myreviews")
    public ResponseEntity<ApiResponse<Slice<ReviewResponseDto>>> getMyReviews(Principal principal
    ,@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<ReviewResponseDto> myReviews = reviewService.getMyReviews(principal.getName(), pageable);
        return ApiResponseHelper.success(myReviews, "내 리뷰 목록 조회 성공");
    }

    @PostMapping("/admin/reply/{storeId}/{reviewId}/createreplies")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReviewReply(
            @Valid @RequestBody ApiRequest<ReviewReplyRequestDto> request,
            @PathVariable("reviewId") Long reviewId,
            Principal principal) {
        ReviewResponseDto responseDto = reviewService.createReviewReply(reviewId, request.getData(), principal.getName());
        return ApiResponseHelper.success(responseDto, "리뷰 답변이 작성되었습니다");
    }

    @GetMapping("/stores/reviews/{storeId}/storesreviews")
    public ResponseEntity<ApiResponse<Slice<ReviewResponseDto>>> getStoreReviews(
            @PathVariable("storeId") Long storeId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Slice<ReviewResponseDto> storeReviews = reviewService.getStoreReviews(storeId, pageable);
        return ApiResponseHelper.success(storeReviews, "매장 리뷰 목록 조회 성공");
    }
}
