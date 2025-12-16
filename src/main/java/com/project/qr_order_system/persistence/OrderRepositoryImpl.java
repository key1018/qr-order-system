package com.project.qr_order_system.persistence;

import com.project.qr_order_system.dto.admin.AdminOrderSearchDto;
import com.project.qr_order_system.model.OrderEntity;
import com.project.qr_order_system.model.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.EntityPath;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.project.qr_order_system.model.QOrderEntity.orderEntity;
import static com.project.qr_order_system.model.QOrderItemEntity.orderItemEntity;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderEntity> searchOrders(AdminOrderSearchDto searchDto) {
        return queryFactory
                .selectFrom(orderEntity)

                // EntityPath<?>라는 와일드카드를 붙여서 원시 타입 에러 해결
                .leftJoin((EntityPath<?>) orderEntity.user).fetchJoin()
                // 리스트는 별칭 사용
                .leftJoin(orderEntity.orderItems, orderItemEntity).fetchJoin()
                .leftJoin((EntityPath<?>) orderItemEntity.product).fetchJoin()
                .where(
                        storeEq(searchDto.getStoreId()),
                        statusEq(searchDto.getStatus()),
                        dateBetween(searchDto.getStartDate(), searchDto.getEndDate()),
                        priceBetween(searchDto.getMinPrice(), searchDto.getMaxPrice()),
                        userIdEq(searchDto.getUserId()),
                        menuIdEq(searchDto.getMenuId())
                )
                .distinct()
                .orderBy(orderEntity.createdAt.desc())
                .fetch();
    }

    // === 조건 메서드들 ===

    private BooleanExpression storeEq(Long storeId) {
        // store.id 접근이 안 되면 아래처럼 쓰세요
        return storeId != null ? orderEntity.store.id.eq(storeId) : null;
    }

    private BooleanExpression statusEq(OrderStatus status) {
        return status != null ? orderEntity.status.eq(status) : null;
    }

    private BooleanExpression dateBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) return null;
        if (start != null && end != null) return orderEntity.createdAt.between(start, end);
        if (start != null) return orderEntity.createdAt.goe(start);
        return orderEntity.createdAt.loe(end);
    }

    private BooleanExpression priceBetween(Integer min, Integer max) {
        if (min == null && max == null) return null;
        if (min != null && max != null) return orderEntity.totalPrice.between(min, max);
        if (min != null) return orderEntity.totalPrice.goe(min);
        return orderEntity.totalPrice.loe(max);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? orderEntity.user.id.eq(userId) : null;
    }

    private BooleanExpression menuIdEq(Long menuId) {
        return menuId != null ? orderItemEntity.product.id.eq(menuId) : null;
    }
}