package com.project.qr_order_system.dto.store;

import com.project.qr_order_system.dto.product.ProductResponseDto;
import com.project.qr_order_system.model.StoreType;

import java.util.List;

/**
 * 매장 정보 조회 응답
 */
public class StoreResponseDto {
    private Long id;
    private String storeName;
    private StoreType storeType;
    private List<ProductResponseDto> products; // 매장 상품 목록
}
