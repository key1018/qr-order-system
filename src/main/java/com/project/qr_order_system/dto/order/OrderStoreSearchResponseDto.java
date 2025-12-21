package com.project.qr_order_system.dto.order;

import com.project.qr_order_system.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class OrderStoreSearchResponseDto {

    // 주문 식별 정보
    private Long orderId;            // 주문 고유 번호
    private LocalDateTime orderedAt; // 주문 일시 (최신순 정렬용)
    private OrderStatus orderStatus; // 현재 처리 상태

    // 고객 정보
    private Long userId;             // 고객 고유 ID (단골 식별용)
    private String userName;         // 주문자 이름 (UserEntity.name)
    private String userEmail;        // 주문자 이메일 (UserEntity.email)

    // 매장 운영 정보
    private Long storeId;            // 가게 고유 번호
    private String storeName;        // 가게 이름 (여러 매장 운영 시 구분용)
    private Integer tableNumber;     // 테이블 번호 (서빙 위치)
    private boolean isTakeOut;       // 포장 여부 (tableNumber가 null이면 포장으로 간주)

    // 주문 내역
    private List<OrderItemResponseDto> orderItems; // 상세 메뉴 리스트

    // 결제 및 취소 정보
    private Integer totalPrice;      // 결제 금액
    private String usedCardName;     // 결제 카드
    private String cancelReason;     // 취소 사유 (거절/취소 시)

    @Builder
    public OrderStoreSearchResponseDto(Long orderId, LocalDateTime orderedAt, OrderStatus orderStatus, Long userId, String userName, String userEmail, Long storeId, String storeName, Integer tableNumber, boolean isTakeOut, List<OrderItemResponseDto> orderItems, Integer totalPrice, String usedCardName, String cancelReason) {
        this.orderId = orderId;
        this.orderedAt = orderedAt;
        this.orderStatus = orderStatus;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.storeId = storeId;
        this.storeName = storeName;
        this.tableNumber = tableNumber;
        this.isTakeOut = isTakeOut;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
        this.usedCardName = usedCardName;
        this.cancelReason = cancelReason;
    }
}
