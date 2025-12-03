package com.project.qr_order_system.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewReplyRequestDto {

    @NotBlank(message = "답변 내용을 입력해주세요.")
    @Size(max = 1000, message = "1,000자까지만 작성 가능합니다.")
    private String reviewReplyContent;

}
