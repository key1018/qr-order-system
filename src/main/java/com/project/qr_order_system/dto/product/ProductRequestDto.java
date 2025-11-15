package com.project.qr_order_system.dto.product;

import ch.qos.logback.core.pattern.color.BoldWhiteCompositeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 상품 등록/수정 요청 (관리자용)
 */
@Getter
@NoArgsConstructor
public class ProductRequestDto {
    private String productName;
    private Integer price;
    private Integer stock;
    private String imageUrl;
    private String available;
}
