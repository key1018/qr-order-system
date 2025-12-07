package com.project.qr_order_system.dto.review;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

    private Long reviewId;
    private String reviewContent;
    private String reviewImageUrl;
    private String reviewerName; // 작성자
    private LocalDateTime reviewDate; // 리뷰 작성 일자
    private List<ReviewItemInfo> reviewItems;

    private String reviewReplyContent;
    private LocalDateTime replyDate;

    private Long storeId;
    private String storeName;
    private String storeImage; // 가게 대표 사진

    @Getter
    @Builder
    public static class ReviewItemInfo {
        private String productName;
        private Integer rating;
    }
}
