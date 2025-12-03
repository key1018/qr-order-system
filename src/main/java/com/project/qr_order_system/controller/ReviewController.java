package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.review.ReviewReplyRequestDto;
import com.project.qr_order_system.dto.review.ReviewResponseDto;
import com.project.qr_order_system.dto.review.ReviewCreateRequestDto;
import com.project.qr_order_system.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/qrorder")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/user/reviews/createreviews")
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewCreateRequestDto requestDto, Principal principal) {
        ReviewResponseDto responseDto = reviewService.createReview(requestDto, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/admin/reply/{storeId}/{reviewId}/createreplies")
    public ResponseEntity<ReviewResponseDto> createReviewReply(
            @Valid @RequestBody ReviewReplyRequestDto requestDto,
            @PathVariable("reviewId") Long reviewId,
            Principal principal) {
        ReviewResponseDto responseDto = reviewService.createReviewReply(reviewId, requestDto, principal.getName());
        return ResponseEntity.ok(responseDto);
    }
}
