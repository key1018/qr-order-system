package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.payment.PaymentCardRegisterRequestDto;
import com.project.qr_order_system.dto.payment.PaymentCardResponseDto;
import com.project.qr_order_system.service.PaymentCardService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/qrorder/users/card")
@AllArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    /**
     * 카드 등록 (고객용)
     */
    @PostMapping("/registerCard")
    public ResponseEntity<PaymentCardResponseDto> registerCard(@RequestBody PaymentCardRegisterRequestDto requestDto, Principal principal) {

        PaymentCardResponseDto responseDto = paymentCardService.registerCard(requestDto, principal.getName());

        return ResponseEntity.ok(responseDto);
    }

}
