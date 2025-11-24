package com.project.qr_order_system.controller;

import com.project.qr_order_system.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/qrorder")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 가게용
     */
    @GetMapping(value = "/admin/orders/notifications/subscribe/{storeId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable("storeId") Long storeId, Principal principal) {
        return notificationService.subscribe(storeId, principal.getName());
    }

    /**
     * 고객용
     */
    @GetMapping(value = "/user/orders/notifications/subscribe/{storeId}/{orderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter customerSubscribe(@PathVariable("storeId") Long storeId, @PathVariable("orderId") Long orderId, Principal principal) {
        return notificationService.customerSubscribe(storeId, orderId, principal.getName());
    }
}
