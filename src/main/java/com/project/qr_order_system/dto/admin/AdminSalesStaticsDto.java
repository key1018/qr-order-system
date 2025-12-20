package com.project.qr_order_system.dto.admin;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminSalesStaticsDto {
    private String date; // 날짜 (2025-12-17 또는 2025-12)
    private Long totalSales;
    private Long orderCount;

    @QueryProjection
    public AdminSalesStaticsDto(String date, Long totalSales,Long orderCount) {
        this.date = date;
        this.totalSales = totalSales;
        this.orderCount = orderCount;
    }
}
