package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.order.OrderRejectRequestDto;
import com.project.qr_order_system.dto.order.OrderRequestDto;
import com.project.qr_order_system.dto.order.OrderResponseDto;
import com.project.qr_order_system.model.OrderStatus;
import com.project.qr_order_system.model.RejectReason;
import com.project.qr_order_system.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
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
    @PostMapping("/users/orders/createOrders")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto requestDto, Principal principal) {
        OrderResponseDto responseDto = orderService.addOrder(requestDto, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 취소 (고객용)
     */
    @PostMapping("/users/orders/{storeId}/{orderId}/cancelOrders")
    public ResponseEntity<OrderResponseDto> cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.cancelOrder(orderId, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 목록 조회 (전체/상태별) : 고객용
     */
    @GetMapping("/user/orders/orderList")
    public ResponseEntity<List<OrderResponseDto>> getUserOrderStatusList(
            @RequestParam(value = "status", required = false) OrderStatus status,
            Principal principal
    ) {

        List<OrderResponseDto> responseDtos;

        if(status == null) {
            // 전체 조회
            responseDtos = orderService.getOrdersByUser(principal.getName());
        } else {
            // 상태별 조회
            responseDtos = orderService.getOrdersStatusByUser(principal.getName(), status);
        }

        return ResponseEntity.ok(responseDtos);
    }

    /**
     * 주문 취소 (관리자용)
     */
    @PostMapping("/admin/orders/{storeId}/{orderId}/rejectOrders")
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
    @PostMapping("/admin/orders/{storeId}/{orderId}/acceptOrders")
    public ResponseEntity<OrderResponseDto> acceptOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.acceptOrder(storeId, orderId, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 완료 (관리자용)
     */
    @PostMapping("/admin/orders/{storeId}/{orderId}/completeOrders")
    public ResponseEntity<OrderResponseDto> completeOrder(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        OrderResponseDto responseDto = orderService.completeOrder(storeId, orderId, principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * 주문 목록 조회 (전체/상태별) : 관리자용
     */
    @GetMapping("/admin/orders/{storeId}/orderList")
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
}
