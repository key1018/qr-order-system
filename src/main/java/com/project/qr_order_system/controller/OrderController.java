package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.order.OrderRequestDto;
import com.project.qr_order_system.dto.order.OrderResponseDto;
import com.project.qr_order_system.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/qrorder/users/order")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    @PostMapping("/createOrders")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto requestDto, Principal principal) {
        OrderResponseDto responseDto = orderService.addOrder(requestDto, principal.getName());
        return ResponseEntity.ok(responseDto);
    }
}
