package com.project.qr_order_system.persistence;

import com.project.qr_order_system.dto.admin.*;
import com.project.qr_order_system.model.OrderEntity;
import com.project.qr_order_system.model.OrderStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.EntityPath;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.dsl.Expressions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.project.qr_order_system.model.QOrderEntity.orderEntity;
import static com.project.qr_order_system.model.QOrderItemEntity.orderItemEntity;

@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 상세 조회
     */
    @Override
    public Page<OrderEntity> searchOrders(AdminOrderSearchDto searchDto, Pageable pageable) {
        List<OrderEntity> content = queryFactory
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
                        menuIdEq(searchDto.getMenuId()),
                        userNameContains(searchDto.getUserName()),
                        userEmailEq(searchDto.getUserEmail())
                )
                .distinct()
                .orderBy(orderEntity.createdAt.desc())
                .offset(pageable.getOffset()) // 페이지 번호 (0부터 시작)
                .limit(pageable.getPageSize()) // 페이지 당 개수
                .fetch();

        // 전체 개수
        Long totalCount = queryFactory
                .select(orderEntity.countDistinct())
                .from(orderEntity)
                .leftJoin(orderEntity.user)
                .leftJoin(orderEntity.orderItems, orderItemEntity)
                .where(
                        storeEq(searchDto.getStoreId()),
                        statusEq(searchDto.getStatus()),
                        dateBetween(searchDto.getStartDate(), searchDto.getEndDate()),
                        priceBetween(searchDto.getMinPrice(), searchDto.getMaxPrice()),
                        userIdEq(searchDto.getUserId()),
                        menuIdEq(searchDto.getMenuId()),
                        userNameContains(searchDto.getUserName()),
                        userEmailEq(searchDto.getUserEmail())
                )
                .fetchOne();

        if(totalCount == null) totalCount = 0L;

        // List + TotalCount = Page 포장해서 리턴
        return new PageImpl<>(content, pageable, totalCount);
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

    private BooleanExpression priceBetween(Long min, Long max) {
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

    private BooleanExpression userNameContains(String userName) {
        return (userName != null && !userName.isEmpty())
                ? orderEntity.user.name.contains(userName)
                : null;
    }

    private BooleanExpression userEmailEq(String userEmail) {
        return userEmail != null ? orderEntity.user.email.eq(userEmail) : null;
    }

    /**
     * 일별 조회
     */
    @Override
    public List<AdminSalesStaticsDto> getDailyStatics(Long storeId, LocalDate startDate, LocalDate endDate) {
        StringTemplate day = Expressions.stringTemplate(
                "TO_CHAR({0}, 'YYYY-MM-DD')",
                orderEntity.createdAt
        );
        return getStoreStatics(storeId,startDate,endDate,day);
    }

    /**
     * 월별 조회
     */
    @Override
    public List<AdminSalesStaticsDto> getMonthlyStatics(Long storeId, LocalDate startDate, LocalDate endDate) {
        StringTemplate month = Expressions.stringTemplate(
                "TO_CHAR({0}, 'YYYY-MM')",
                orderEntity.createdAt
        );
        return getStoreStatics(storeId,startDate,endDate,month);
    }

    /**
     * 월별, 일별 공통 쿼리
     */
    public List<AdminSalesStaticsDto> getStoreStatics(Long storeId, LocalDate startDate, LocalDate endDate, StringTemplate formattedDate) {
        return queryFactory
                .select(new QAdminSalesStaticsDto(
                        formattedDate,
                        orderEntity.totalPrice.sum().longValue().coalesce(0L),
                        orderEntity.count()
                        )
                )
                .from(orderEntity)
                .where(
                        orderEntity.store.id.eq(storeId),
                        orderEntity.status.eq(OrderStatus.DONE),
                        orderEntity.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
                )
                .groupBy(formattedDate) // 날짜별로 묶기
                .orderBy(formattedDate.asc()) // 날짜 순서대로
                .fetch()
                ;
    }


    /**
     * 메뉴 순위
     */
    @Override
    public List<AdminMenuSalesStatisticsDto> getMenuSalesStatics(Long storeId, LocalDate startDate, LocalDate endDate, Integer limit, boolean isAsc) {

        return queryFactory.
                select(new QAdminMenuSalesStatisticsDto(
                        orderItemEntity.product.id,
                        orderItemEntity.product.productName,
                        orderItemEntity.quantity.sum().longValue().coalesce(0L),
                        orderItemEntity.totalPrice.sum().longValue().coalesce(0L)
                ))
                .from(orderItemEntity)
                .join(orderItemEntity.order, orderEntity)
                .where(
                        orderEntity.store.id.eq(storeId),
                        orderEntity.status.eq(OrderStatus.DONE),
                        orderEntity.createdAt.between(startDate.atStartOfDay(),endDate.atTime(23, 59, 59))
                )
                .groupBy(orderItemEntity.product.id, orderItemEntity.product.productName)
                .orderBy(isAsc ? orderItemEntity.totalPrice.sum().longValue().asc() : orderItemEntity.totalPrice.sum().longValue().desc())
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .fetch();

    }
}