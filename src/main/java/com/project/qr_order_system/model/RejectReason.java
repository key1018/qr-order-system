package com.project.qr_order_system.model;

import lombok.Getter;

/**
 * 주문 취소/거절 사유 (관리자용)
 */
@Getter
public enum RejectReason {
    // ================ 재고 복구 가능 ================

    // 고객 관련
    CUSTOMER_REQUEST_BEFORE_COOKING("고객 요청",true, "cancel"), // 조리 전/단순 변심
    CUSTOMER_MISORDER("메뉴 착오", true, "cancel"), // 고객의 잘못된 주문
    // 가게 관련
    STORE_OPERATIONAL_ISSUE("가게 사정", true, "reject"), // 주문 폭주/운영 불가/실수
    CLOSING_TIME("영업 종료 / 브레이크 타임", true, "reject"),
    // 설비 관련 (조리 전에 고장)
    EQUIPMENT_FAILURE_BEFORE_COOKING("설비 고장으로 조리 불가", true, "reject"), // 조리 전/재료 보존

    // ================ 재고 복구 불가능 ================

    // 고객 관련
    CUSTOMER_REQUEST_AFTER_COOKING("고객 요청", false, "cancel"), // 이미 조리됨/폐기
    CUSTOMER_NO_SHOW("고객 미수령으로 폐기", false, "cancel"),
    // 재고 관련
    OUT_OF_STOCK("재료 소진", false, "reject"), // 재고 부족
    // 사고/과실 관련
    STORE_ACCIDENT("조리 사고/재료 손상", false, "reject"), // 가게 과실
    QUALITY_ISSUE("품질 문제로 폐기 (온도/시간 초과 등)", false, "reject"),
    // 설비 관련 (조리 중에 고장)
    EQUIPMENT_FAILURE_AFTER_COOKING("설비 고장으로 조리 불가",false, "reject"); // 조리 중/재료 보존 불가능

    private final String description;
    private final boolean restoreStock;
    private final String type;

    RejectReason(String description, boolean restoreStock, String type) {
        this.description = description;
        this.restoreStock = restoreStock;
        this.type = type;
    }
}
