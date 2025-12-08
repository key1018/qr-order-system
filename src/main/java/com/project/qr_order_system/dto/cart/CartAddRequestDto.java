package com.project.qr_order_system.dto.cart;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddRequestDto {
    private Long productId;
    private Integer quantity;
}
