package com.project.qr_order_system.controller;

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
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewCreateRequestDto requestDto, Principal principal) {
        ReviewResponseDto responseDto = reviewService.createReview(requestDto, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/users/reviews/myreviews")
    public ResponseEntity<Slice<ReviewResponseDto>> getMyReviews(Principal principal
    ,@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Slice<ReviewResponseDto> myReviews = reviewService.getMyReviews(principal.getName(), pageable);
        return ResponseEntity.ok(myReviews);
    }

    @PostMapping("/admin/reply/{storeId}/{reviewId}/createreplies")
    public ResponseEntity<ReviewResponseDto> createReviewReply(
            @Valid @RequestBody ReviewReplyRequestDto requestDto,
            @PathVariable("reviewId") Long reviewId,
            Principal principal) {
        ReviewResponseDto responseDto = reviewService.createReviewReply(reviewId, requestDto, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/stores/reviews/{storeId}/storesreviews")
    public ResponseEntity<Slice<ReviewResponseDto>> getStoreReviews(
            @PathVariable("storeId") Long storeId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Slice<ReviewResponseDto> storeReviews = reviewService.getStoreReviews(storeId, pageable);
        return ResponseEntity.ok(storeReviews);
    }
}
