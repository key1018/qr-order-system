package com.project.qr_order_system.dto.review;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewCreateRequestDto {

    @NotNull(message = "주문 상세 ID는 필수입니다.")
    private Long orderId;

    @NotBlank(message = "리뷰 내용은 필수입니다.")
    @Size(max = 1000, message = "1,000자까지만 작성 가능합니다.")
    private String reviewContent;

    private String reviewImageUrl; // 사진 이미지 (선택 사항)

    @NotEmpty(message = "최소 하나의 메뉴는 평가해야 합니다.")
    @Valid // 내부 필드값까지 검증 (orderItemId, rating이 null이면 오류 뱉음)
    private List<ReviewItemRequestDto> ratingList;

    @Getter
    @NoArgsConstructor
    public static class ReviewItemRequestDto {
        @NotNull
        private Long orderItemId;
        @NotNull
        private Integer rating;
    }
}
