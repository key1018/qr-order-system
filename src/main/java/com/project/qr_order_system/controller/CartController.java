package com.project.qr_order_system.controller;

import com.project.qr_order_system.dto.cart.CartAddRequestDto;
import com.project.qr_order_system.dto.cart.CartAddResponseDto;
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

    @PostMapping("/user/cart/addcart")
    public ResponseEntity<String> addCart(@RequestBody CartAddRequestDto requestDto
                                          , Principal principal) {
        cartService.addItemToCart(requestDto, principal.getName());
        return ResponseEntity.ok("장바구니에 상품을 담았습니다.");
    }

    @GetMapping("/user/cart/getcartlist")
    public ResponseEntity<List<CartAddResponseDto>> getCartLists(Principal principal) {
        return ResponseEntity.ok(cartService.getCartList(principal.getName()));
    }

    @PatchMapping("/user/cart/updatecartitemquantity")
    public ResponseEntity<String> updateCartItemQuantity(@RequestBody CartAddRequestDto requestDto
                                                        , Principal principal) {
        cartService.updateCartItemQuantity(requestDto, principal.getName());
        return ResponseEntity.ok("수량이 변경되었습니다.");
    }

    @DeleteMapping("/user/cart/{productId}/deleteproductidfromcart")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable("productId") Long productId
                                                        , Principal principal) {
        cartService.deleteCart(productId, principal.getName());
        return ResponseEntity.ok("상품이 삭제되었습니다.");
    }

    @DeleteMapping("/user/cart/clearcart")
    public ResponseEntity<String> clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return ResponseEntity.ok("장바구니를 비웠습니다.");
    }

}
