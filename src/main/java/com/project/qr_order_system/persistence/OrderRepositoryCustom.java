package com.project.qr_order_system.persistence;

import com.project.qr_order_system.dto.admin.AdminMenuSalesStatisticsDto;
import com.project.qr_order_system.dto.admin.AdminOrderSearchDto;
import com.project.qr_order_system.dto.admin.AdminSalesStaticsDto;
import com.project.qr_order_system.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepositoryCustom {
    // [관리자용] 상세 조회
    Page<OrderEntity> searchOrders(AdminOrderSearchDto searchDto, Pageable pageable);

    // [관리자용] 일별 매출 조회
    List<AdminSalesStaticsDto> getDailyStatics(Long storeId, LocalDate startDate, LocalDate endDate);

    // [관리자용] 월별 매출 조회
    List<AdminSalesStaticsDto> getMonthlyStatics(Long storeId, LocalDate startDate, LocalDate endDate);

    // [관리자용] 메뉴 순위 (전체, 상위5, 하위5)
    List<AdminMenuSalesStatisticsDto> getMenuSalesStatics(Long storeId,
                                                          LocalDate startDate,
                                                          LocalDate endDate,
                                                          Integer limit,
                                                          boolean isAes);
}

