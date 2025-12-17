package com.project.qr_order_system.persistence;

import com.project.qr_order_system.dto.admin.AdminOrderSearchDto;
import com.project.qr_order_system.model.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderRepositoryCustom {
    Page<OrderEntity> searchOrders(AdminOrderSearchDto searchDto, Pageable pageable);
}
