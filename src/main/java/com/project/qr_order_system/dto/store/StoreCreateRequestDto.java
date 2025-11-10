package com.project.qr_order_system.dto.store;

import com.project.qr_order_system.model.StoreType;

/**
 * 매장 생성 요청 (관리자용)
 */
public class StoreCreateRequestDto {
    private String storeName;
    private StoreType storeType;
}
