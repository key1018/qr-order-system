package com.project.qr_order_system.dto.store;

import com.project.qr_order_system.model.StoreType;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 생성 요청 (관리자용)
 */
@Getter
@NoArgsConstructor
public class StoreCreateRequestDto {
    private String storeName;
    private StoreType storeType;
}
