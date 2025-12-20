package com.project.qr_order_system.dto.admin;

import com.project.qr_order_system.model.StoreType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminMenuSalesStatisticsDto {
    private Long productId;
    private String productName;
    private Long totalQuantity;
    private Long totalSales;

    @QueryProjection
    public AdminMenuSalesStatisticsDto(Long productId, String productName, Long totalQuantity, Long totalSales) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalSales = totalSales;
    }
}
