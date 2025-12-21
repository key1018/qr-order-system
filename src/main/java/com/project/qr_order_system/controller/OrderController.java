package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.admin.AdminMenuSalesStatisticsDto;
import com.project.qr_order_system.dto.admin.AdminOrderSearchDto;
import com.project.qr_order_system.dto.admin.AdminSalesStaticsDto;
import com.project.qr_order_system.dto.common.ApiRequest;
import com.project.qr_order_system.dto.common.ApiResponse;
import com.project.qr_order_system.dto.common.ApiResponseHelper;
import com.project.qr_order_system.dto.order.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/users/orders/createorders")
    public ResponseEntity<ApiResponse<OrderResponseDto>> createOrder(@RequestBody ApiRequest<OrderRequestDto> request, Principal principal) {
        OrderResponseDto responseDto = orderService.addOrder(request.getData(), principal.getName());
        return ApiResponseHelper.success(responseDto, "주문이 성공적으로 생성되었습니다");
    }

    /**
     * 주문 취소 (고객용)
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/users/orders/{storeId}/{orderId}/cancelorders")
    public ResponseEntity<ApiResponse<OrderResponseDto>> cancelOrder(
            @PathVariable("storeId") Long storeId, 
            @PathVariable("orderId") Long orderId,
             Principal principal) {
        OrderResponseDto responseDto = orderService.cancelOrder(storeId, orderId, principal.getName());
        return ApiResponseHelper.success(responseDto, "주문이 취소되었습니다");
    }

    /**
     * 주문 목록 조회 (전체/상태별) : 고객용
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/users/orders/orderlist")
    public ResponseEntity<ApiResponse<Slice<OrderSearchResponseDto>>> getUserOrderStatusList(
            @RequestParam(value = "status", required = false) OrderStatus status,
            Principal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Slice<OrderSearchResponseDto> myOrders = orderService.getOrdersByUser(principal.getName(), status, pageable);

        return ApiResponseHelper.success(myOrders, "주문 목록 조회 성공");
    }

    /**
     * 주문 취소 (관리자용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/orders/{storeId}/{orderId}/rejectorders")
    public ResponseEntity<ApiResponse<OrderResponseDto>> rejectOrder(
            @PathVariable("storeId") Long storeId,
            @PathVariable("orderId") Long orderId,
            Principal principal,
            @RequestBody ApiRequest<OrderRejectRequestDto> request
            ) {
        OrderResponseDto responseDto = orderService.rejectOrder(storeId, orderId, principal.getName(), request.getData().getReason());
        return ApiResponseHelper.success(responseDto, "주문이 거절되었습니다");
    }

    /**
     * 주문 승낙 (관리자용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/orders/{storeId}/{orderId}/acceptorders")
    public ResponseEntity<ApiResponse<OrderResponseDto>> acceptOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.acceptOrder(storeId, orderId, principal.getName());
        return ApiResponseHelper.success(responseDto, "주문이 승낙되었습니다");
    }

    /**
     * 주문 조리 완료 (관리자용)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/orders/{storeId}/{orderId}/completeorders")
    public ResponseEntity<ApiResponse<OrderResponseDto>> completeOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.completeOrder(storeId, orderId, principal.getName());
        return ApiResponseHelper.success(responseDto, "주문 조리가 완료되었습니다");
    }

    /**
     * 주문 최종 완료 (관리자용)
     * 수동처리
     * 상태 : READY -> DONE
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/orders/{storeId}/{orderId}/finishorders")
    public ResponseEntity<ApiResponse<OrderResponseDto>> finishOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.finishOrder(storeId, orderId, principal.getName());
        return ApiResponseHelper.success(responseDto, "주문이 최종 완료되었습니다");
    }

    /**
     * 주문 목록 조회 (전체/상태별) : 관리자용
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders/{storeId}/orderlist")
    public ResponseEntity<ApiResponse<Slice<OrderStoreSearchResponseDto>>> getOrderStatusList(
            @PathVariable("storeId") Long storeId,
            @RequestParam(value = "status", required = false) OrderStatus status,
            Principal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable

    ) {
        Slice<OrderStoreSearchResponseDto> storeOrders = orderService.getOrdersByStore(storeId, status, principal.getName(),pageable);
        return ApiResponseHelper.success(storeOrders, "주문 목록 조회 성공");
    }

    /**
     * 상세 주문 조회
     * ex) /qrorder/admin/search/orders?storeId=1&page=0&size=10
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search/orders")
    public ResponseEntity<ApiResponse<List<OrderResponseDto>>> searchOrders(
            @ModelAttribute AdminOrderSearchDto searchDto,
            Principal principal,
            Pageable pageable
    ){
        log.info("관리자 상세 검색 요청 - StoreId: {}, 조건: {}", searchDto.getStoreId(), searchDto);

        return ApiResponseHelper.success(orderService.searchOrders(searchDto, principal.getName(), pageable), "주문 검색 성공");
    }

    /**
     * 일별 매출 조회
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search/dailysales")
    public ResponseEntity<ApiResponse<List<AdminSalesStaticsDto>>> getDailySales(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            Principal principal
    ){
        return ApiResponseHelper.success(orderService.getDailySales(storeId, startDate, endDate, principal.getName()), "일별 매출 조회 성공");
    }

    /**
     * 월별 매출 조회
     *
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search/monthlysales")
    public ResponseEntity<ApiResponse<List<AdminSalesStaticsDto>>> getMonthlySales(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            Principal principal){
        return ApiResponseHelper.success(orderService.getMonthlySales(storeId, startDate, endDate, principal.getName()), "월별 매출 조회 성공");
    }

    /**
     * 메뉴 매출 순위 조회
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/search/menusales")
    public ResponseEntity<ApiResponse<List<AdminMenuSalesStatisticsDto>>> getMenuSalesStatics(
            @RequestParam("storeId") Long storeId,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam(value = "type", defaultValue = "ALL") String type,
            Principal principal
    ){
        return ApiResponseHelper.success(orderService.getMenuSalesStatics(storeId, startDate,endDate,principal.getName(),type), "메뉴 매출 순위 조회 성공");
    }

}
