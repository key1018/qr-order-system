package com.project.qr_order_system.dto.order;

import com.project.qr_order_system.model.OrderStatus;
import com.project.qr_order_system.model.StoreType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 주문 목록 조회 응답 DTO
 * 페이징 처리를 위한 주문 검색 결과용
 */
@Getter
public class OrderSearchResponseDto {
    
    // 가게 정보
    private Long storeId;                    // 가게 ID
    private String storeName;                // 가게명
    private StoreType storeType;             // 가게 타입 (DINE_IN, TAKE_OUT 등)
    
    // 주문 기본 정보
    private Long orderId;                    // 주문번호
    private OrderStatus orderStatus;         // 주문상태 (ORDERED, IN_PROGRESS, READY, DONE, CANCELED, REJECTED)
    private Integer totalPrice;              // 총 주문 가격
    private LocalDateTime createdAt;         // 주문 생성일시
    
    // 주문 상세 정보
    private List<OrderItemResponseDto> orderItems;  // 주문 상품 목록 (상품명, 수량, 각 상품 가격 포함)
    
    // 테이블 주문 정보 (nullable)
    private Integer tableNumber;             // 테이블 번호 (테이블 주문 시, 테이크아웃이면 null)
    
    // 취소/거절 정보 (nullable)
    private String cancelReason;             // 취소사유 (취소/거절 시에만 값 존재)
    
    // 결제 정보
    private String usedCardName;             // 사용한 카드명

    @Builder
    public OrderSearchResponseDto(
            Long storeId,
            String storeName,
            StoreType storeType,
            Long orderId,
            OrderStatus orderStatus,
            Integer totalPrice,
            LocalDateTime createdAt,
            List<OrderItemResponseDto> orderItems,
            Integer tableNumber,
            String cancelReason,
            String usedCardName
    ) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeType = storeType;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.orderItems = orderItems;
        this.tableNumber = tableNumber;
        this.cancelReason = cancelReason;
        this.usedCardName = usedCardName;
    }
}

