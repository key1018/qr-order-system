package com.project.qr_order_system.controller;

import com.google.zxing.WriterException;
import com.project.qr_order_system.dto.store.StoreCreateRequestDto;
import com.project.qr_order_system.dto.store.StoreResponseDto;
import com.project.qr_order_system.model.StoreEntity;
import com.project.qr_order_system.persistence.StoreRepository;
import com.project.qr_order_system.service.QrCodeService;
import com.project.qr_order_system.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

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
    @PostMapping("/createstore")
    public ResponseEntity<StoreResponseDto> createStore(@Valid @RequestBody StoreCreateRequestDto requestDto, Principal principal) {
        System.out.println("StoreCreateRequestDto" + requestDto);
        StoreResponseDto responseDto = storeService.createStore(requestDto,principal.getName());
        return ResponseEntity.ok(responseDto);
    }

    /**
     * QR 코드 생성 API
     */
    @GetMapping(
            value = "/{storeId}/qr-code",
            produces = MediaType.IMAGE_PNG_VALUE // (응답 타입 = PNG 이미지)
    )
    public ResponseEntity<byte[]> getQrCode(
            @PathVariable("storeId") Long storeId,
            @RequestParam(value = "tableNumber", required = false) Integer tableNumber, // (tableNumber는 필수 아님)
            Principal principal
    ) throws WriterException, IOException {
        try {
            // 본인 매장이 맞는지 검증
            storeService.validateStoreOwner(principal.getName(), storeId);

            // QR 코드 이미지 생성
            byte[] qrCodeImage = qrCodeService.createQrCodeImage(storeId, tableNumber);

            return ResponseEntity.ok(qrCodeImage);

        } catch (SecurityException e) {
            // 403 Forbidden (권한 없음)
            log.warn("매장 접근 권한 없음: user={}, storeId={}", principal.getName(), storeId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException e) {
            // 404 Not Found (매장 없음 등)
            log.warn("잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
