package com.project.qr_order_system.controller;

import com.google.zxing.WriterException;
import com.project.qr_order_system.dto.common.ApiRequest;
import com.project.qr_order_system.dto.common.ApiResponse;
import com.project.qr_order_system.dto.common.ApiResponseHelper;
import com.project.qr_order_system.dto.store.StoreCreateRequestDto;
import com.project.qr_order_system.dto.store.StoreResponseDto;
import com.project.qr_order_system.service.QrCodeService;
import com.project.qr_order_system.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/qrorder/admin/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final QrCodeService qrCodeService;

    /**
     * 매장 생성 API
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/createstore")
    public ResponseEntity<ApiResponse<StoreResponseDto>> createStore(@Valid @RequestBody ApiRequest<StoreCreateRequestDto> request, Principal principal) {
        System.out.println("StoreCreateRequestDto" + request.getData());
        StoreResponseDto responseDto = storeService.createStore(request.getData(),principal.getName());
        return ApiResponseHelper.success(responseDto, "매장이 성공적으로 생성되었습니다");
    }

    /**
     * QR 코드 생성 API
     * 이미지 바이너리를 반환하므로 에러는 예외로 던져서 GlobalExceptionHandler에서 처리
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(
            value = "/{storeId}/qr-code",
            produces = MediaType.IMAGE_PNG_VALUE // (응답 타입 = PNG 이미지)
    )
    public ResponseEntity<byte[]> getQrCode(
            @PathVariable("storeId") Long storeId,
            @RequestParam(value = "tableNumber", required = false) Integer tableNumber, // (tableNumber는 필수 아님)
            Principal principal
    ) throws WriterException, IOException {
        // 본인 매장이 맞는지 검증 (역할 체크는 @PreAuthorize로 처리)
        // SecurityException이나 IllegalArgumentException은 GlobalExceptionHandler에서 처리됨
        storeService.validateStoreOwner(principal.getName(), storeId);

        // QR 코드 이미지 생성
        byte[] qrCodeImage = qrCodeService.createQrCodeImage(storeId, tableNumber);

        return ResponseEntity.ok(qrCodeImage);
    }
}
