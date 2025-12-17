package com.project.qr_order_system.dto.admin;

import com.project.qr_order_system.model.OrderStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminOrderSearchDto {
    private Long storeId;
    private OrderStatus status;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // ex) 2025-10-25T00:00:00
    private LocalDateTime startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
    private Integer minPrice; // 최소 주문 금액
    private Integer maxPrice; // 최대 주문 금액
    private Long userId; // 특정 회원 조회
    private Long menuId; // 특정 메뉴 조회

    private String userName;
    private String userEmail;
}
