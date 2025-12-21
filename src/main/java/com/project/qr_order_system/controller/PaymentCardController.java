package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.common.ApiRequest;
import com.project.qr_order_system.dto.common.ApiResponse;
import com.project.qr_order_system.dto.common.ApiResponseHelper;
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
    @PostMapping("/registercard")
    public ResponseEntity<ApiResponse<PaymentCardResponseDto>> registerCard(@RequestBody ApiRequest<PaymentCardRegisterRequestDto> request, Principal principal) {

        PaymentCardResponseDto responseDto = paymentCardService.registerCard(request.getData(), principal.getName());

        return ApiResponseHelper.success(responseDto, "카드가 성공적으로 등록되었습니다");
    }

}
