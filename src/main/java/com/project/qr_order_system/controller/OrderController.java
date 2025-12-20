package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.admin.AdminMenuSalesStatisticsDto;
import com.project.qr_order_system.dto.admin.AdminOrderSearchDto;
import com.project.qr_order_system.dto.admin.AdminSalesStaticsDto;
import com.project.qr_order_system.dto.order.OrderRejectRequestDto;
import com.project.qr_order_system.dto.order.OrderRequestDto;
import com.project.qr_order_system.dto.order.OrderResponseDto;
import com.project.qr_order_system.dto.order.OrderSearchResponseDto;
import com.project.qr_order_system.dto.review.ReviewResponseDto;
import com.project.qr_order_system.model.OrderStatus;
import com.project.qr_order_system.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qrorder")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    /**
     * 주문 등록 (고객용)
     */
    @PostMapping("/users/orders/createorders")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto requestDto, Principal principal) {
        OrderResponseDto responseDto = orderService.addOrder(requestDto, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 취소 (고객용)
     */
    @PostMapping("/users/orders/{storeId}/{orderId}/cancelorders")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable("storeId") Long storeId, 
            @PathVariable("orderId") Long orderId,
             Principal principal) {
        OrderResponseDto responseDto = orderService.cancelOrder(storeId, orderId, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 목록 조회 (전체/상태별) : 고객용
     */
    @GetMapping("/users/orders/orderlist")
    public ResponseEntity<Slice<OrderSearchResponseDto>> getUserOrderStatusList(
            @RequestParam(value = "status", required = false) OrderStatus status,
            Principal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Slice<OrderSearchResponseDto> myOrders = orderService.getOrdersByUser(principal.getName(), status, pageable);

        return ResponseEntity.ok(myOrders);
    }

    /**
     * 주문 취소 (관리자용)
     */
    @PatchMapping("/admin/orders/{storeId}/{orderId}/rejectorders")
    public ResponseEntity<OrderResponseDto> rejectOrder(
            @PathVariable("storeId") Long storeId,
            @PathVariable("orderId") Long orderId,
            Principal principal,
            @RequestBody OrderRejectRequestDto rejectReason
            ) {
        OrderResponseDto responseDto = orderService.rejectOrder(storeId, orderId, principal.getName(), rejectReason.getReason());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 승낙 (관리자용)
     */
    @PatchMapping("/admin/orders/{storeId}/{orderId}/acceptorders")
    public ResponseEntity<OrderResponseDto> acceptOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.acceptOrder(storeId, orderId, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 조리 완료 (관리자용)
     */
    @PatchMapping("/admin/orders/{storeId}/{orderId}/completeorders")
    public ResponseEntity<OrderResponseDto> completeOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.completeOrder(storeId, orderId, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 최종 완료 (관리자용)
     * 수동처리
     * 상태 : READY -> DONE
     */
    @PatchMapping("/admin/orders/{storeId}/{orderId}/finishorders")
    public ResponseEntity<OrderResponseDto> finishOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.finishOrder(storeId, orderId, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 목록 조회 (전체/상태별) : 관리자용
     */
    @GetMapping("/admin/orders/{storeId}/orderlist")
    public ResponseEntity<List<OrderResponseDto>> getOrderStatusList(
            @PathVariable("storeId") Long storeId,
            @RequestParam(value = "status", required = false) OrderStatus status,
            Principal principal
    ) {

        List<OrderResponseDto> responseDtos;

        if(status == null) {
            // 전체 조회
            responseDtos = orderService.getOrdersByStore(storeId, principal.getName());
        } else {
            // 상태별 조회
            responseDtos = orderService.getOrdersStatusByStore(storeId, principal.getName(), status);
        }

        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 상세 주문 조회
     * ex) /qrorder/admin/search/orders?storeId=1&page=0&size=10
     */
    @GetMapping("/admin/search/orders")
    public ResponseEntity<List<OrderResponseDto>> searchOrders(
            @ModelAttribute AdminOrderSearchDto searchDto,
            Principal principal,
            Pageable pageable
    ){
        log.info("관리자 상세 검색 요청 - StoreId: {}, 조건: {}", searchDto.getStoreId(), searchDto);

        return ResponseEntity.ok(orderService.searchOrders(searchDto, principal.getName(), pageable));
    }

    /**
     * 일별 매출 조회
     */
    @GetMapping("/admin/search/dailysales")
    public ResponseEntity<List<AdminSalesStaticsDto>> getDailySales(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            Principal principal
    ){
        return ResponseEntity.ok(orderService.getDailySales(storeId, startDate, endDate, principal.getName()));
    }

    /**
     * 월별 매출 조회
     *
     */
    @GetMapping("/admin/search/monthlysales")
    public ResponseEntity<List<AdminSalesStaticsDto>> getMonthlySales(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            Principal principal){
        return ResponseEntity.ok(orderService.getMonthlySales(storeId, startDate, endDate, principal.getName()));
    }

    /**
     * 메뉴 매출 순위 조회
     */
    @GetMapping("/admin/search/menusales")
    public ResponseEntity<List<AdminMenuSalesStatisticsDto>> getMenuSalesStatics(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "type", defaultValue = "ALL") String type,
            Principal principal
    ){
        return ResponseEntity.ok(orderService.getMenuSalesStatics(storeId, startDate,endDate,principal.getName(),type));
    }

}
