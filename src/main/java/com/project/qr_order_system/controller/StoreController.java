package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.store.StoreCreateRequestDto;
import com.project.qr_order_system.dto.store.StoreResponseDto;
import com.project.qr_order_system.model.StoreEntity;
import com.project.qr_order_system.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/qrorder/admin/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/createStore")
    public ResponseEntity<StoreResponseDto> createStore(@Valid @RequestBody StoreCreateRequestDto requestDto, Principal principal) {
        System.out.println("StoreCreateRequestDto" + requestDto);
        StoreResponseDto responseDto = storeService.createStore(requestDto,principal.getName());
        return ResponseEntity.ok(responseDto);
    }
}
