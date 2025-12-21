package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.common.ApiRequest;
import com.project.qr_order_system.dto.common.ApiResponse;
import com.project.qr_order_system.dto.common.ApiResponseHelper;
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
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> createProduct(
            @PathVariable("storeId") Long storeId,
            @Valid @RequestBody ApiRequest<List<ProductRequestDto>> request,
            Principal principal
    ) {
        List<ProductResponseDto> responseDtoList = productService.createProduct(request.getData(), storeId, principal.getName());
        return ApiResponseHelper.success(responseDtoList, "상품이 성공적으로 등록되었습니다");
    }

    /**
     * 상품 조회 (관리자)
     */
    @GetMapping("/admin/product/{storeId}/retrieveproductList")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> retrieveProductListForAdmin(
            @PathVariable("storeId") Long storeId,
            Principal principal
    ){
        List<ProductResponseDto> responseDtoList = productService.retrieveProductListForAdmin(storeId,principal.getName());
        return ApiResponseHelper.success(responseDtoList, "상품 목록 조회 성공");
    }

    /**
     * 상품 조회 (고객)
     */
    @GetMapping("/users/product/{storeId}/retrieveproductList")
    public ResponseEntity<ApiResponse<List<ProductResponseDto>>> retrieveProductList(
            @PathVariable("storeId") Long storeId,
            Principal principal
    ){
        List<ProductResponseDto> responseDtoList = productService.retrieveProductListForUser(storeId,principal.getName(),"Y");
        return ApiResponseHelper.success(responseDtoList, "상품 목록 조회 성공");
    }

}
