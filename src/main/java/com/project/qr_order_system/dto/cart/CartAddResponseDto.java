package com.project.qr_order_system.dto.cart;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddResponseDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer price;
    private Integer totalPrice; // price * quantity
}
