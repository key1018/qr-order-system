package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.product.ProductRequestDto;
import com.project.qr_order_system.dto.product.ProductResponseDto;
import com.project.qr_order_system.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qrorder")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 등록 (관리자)
     */
    @PostMapping("/admin/product/{storeId}/createproduct")
    public ResponseEntity<List<ProductResponseDto>> createProduct(
            @PathVariable("storeId") Long storeId,
            @Valid @RequestBody List<ProductRequestDto> requestDto,
            Principal principal
    ) {
        List<ProductResponseDto> responseDtoList = productService.createProduct(requestDto, storeId, principal.getName());
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * 상품 조회 (관리자)
     */
    @PostMapping("/admin/product/{storeId}/retrieveproductList")
    public ResponseEntity<List<ProductResponseDto>> retrieveProductListForAdmin(
            @PathVariable("storeId") Long storeId,
            Principal principal
    ){
        List<ProductResponseDto> responseDtoList = productService.retrieveProductListForAdmin(storeId,principal.getName());
        return ResponseEntity.ok(responseDtoList);
    }

    /**
     * 상품 조회 (고객)
     */
    @PostMapping("/users/product/{storeId}/retrieveproductList")
    public ResponseEntity<List<ProductResponseDto>> retrieveProductList(
            @PathVariable("storeId") Long storeId,
            Principal principal
    ){
        List<ProductResponseDto> responseDtoList = productService.retrieveProductListForUser(storeId,principal.getName(),"Y");
        return ResponseEntity.ok(responseDtoList);
    }

}
