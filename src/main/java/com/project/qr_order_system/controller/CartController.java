package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.cart.CartAddRequestDto;
import com.project.qr_order_system.dto.cart.CartAddResponseDto;
import com.project.qr_order_system.dto.common.ApiRequest;
import com.project.qr_order_system.dto.common.ApiResponse;
import com.project.qr_order_system.dto.common.ApiResponseHelper;
import com.project.qr_order_system.service.CartService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/qrorder")
@AllArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/users/cart/addcart")
    public ResponseEntity<ApiResponse<Void>> addCart(@RequestBody ApiRequest<CartAddRequestDto> request
                                          , Principal principal) {
        cartService.addItemToCart(request.getData(), principal.getName());
        return ApiResponseHelper.success("장바구니에 상품을 담았습니다");
    }

    @GetMapping("/users/cart/getcartlist")
    public ResponseEntity<ApiResponse<List<CartAddResponseDto>>> getCartLists(Principal principal) {
        return ApiResponseHelper.success(cartService.getCartList(principal.getName()), "장바구니 목록 조회 성공");
    }

    @PatchMapping("/users/cart/updatecartitemquantity")
    public ResponseEntity<ApiResponse<Void>> updateCartItemQuantity(@RequestBody ApiRequest<CartAddRequestDto> request
                                                        , Principal principal) {
        cartService.updateCartItemQuantity(request.getData(), principal.getName());
        return ApiResponseHelper.success("수량이 변경되었습니다");
    }

    @DeleteMapping("/users/cart/{productId}/deleteproductidfromcart")
    public ResponseEntity<ApiResponse<Void>> deleteProductFromCart(@PathVariable("productId") Long productId
                                                        , Principal principal) {
        cartService.deleteCart(productId, principal.getName());
        return ApiResponseHelper.success("상품이 삭제되었습니다");
    }

    @DeleteMapping("/users/cart/clearcart")
    public ResponseEntity<ApiResponse<Void>> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ApiResponseHelper.success("장바구니를 비웠습니다");
    }

}
