package com.project.qr_order_system.persistence;

import com.project.qr_order_system.dto.admin.AdminOrderSearchDto;
import com.project.qr_order_system.model.OrderEntity;

import java.util.List;

public interface OrderRepositoryCustom {
    List<OrderEntity> searchOrders(AdminOrderSearchDto searchDto);
}
