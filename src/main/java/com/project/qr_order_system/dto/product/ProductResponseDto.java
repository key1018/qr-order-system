package com.project.qr_order_system.dto.product;

import lombok.Builder;
import lombok.Getter;

/**
 * 상품 정보 응답 (고객용)
 */
@Getter
public class ProductResponseDto {
    private Long id;
    private String productName;
    private Integer price;
    private String imageUrl;

    @Builder
    public ProductResponseDto(Long id, String productName, Integer price, String imageUrl) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.imageUrl = imageUrl;
    }
}
