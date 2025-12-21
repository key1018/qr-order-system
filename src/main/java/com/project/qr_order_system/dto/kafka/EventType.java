package com.project.qr_order_system.dto.kafka;

/**
 * 주문 이벤트 타입 ENUM
 * CREATE, UPDATE, DELETE를 구분하기 위한 타입
 */
public enum EventType {
    CREATE,
    UPDATE,
    DELETE
}

