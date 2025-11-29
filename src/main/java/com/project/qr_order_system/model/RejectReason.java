package com.project.qr_order_system.model;

import lombok.Getter;

/**
 * 주문 취소/거절 사유 (관리자용)
 */
@Getter
public enum RejectReason {
    // 재고 복구 가능
    CUSTOMER_REQUEST_BEFORE_COOKING("고객 요청 (조리 전)",true), // 고객 요청
    STORE_REQUEST("가게 사정", true), // 관리자 요청

    // 재고 복구 불가능
    CUSTOMER_REQUEST_AFTER_COOKING("고객 요청 (조리 중/조리 후)", false), // 고객 요청
    OUT_OF_STOCK("재료 소진/페기", false); // 관리자 요청 (재료가 상했거나 실수한 경우 - 가게 사정)

    private final String description;
    private final boolean restoreStock;


    RejectReason(String description, boolean restoreStock) {
        this.description = description;
        this.restoreStock = restoreStock;
    }
}
