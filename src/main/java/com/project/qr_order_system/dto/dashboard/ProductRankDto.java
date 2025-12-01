package com.project.qr_order_system.dto.dashboard;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRankDto {
    private String productName; // 상품명
    private Long totalQuantity; // 총 판매량
    private Long totalSalesPrice; // 총 판매금액
}
