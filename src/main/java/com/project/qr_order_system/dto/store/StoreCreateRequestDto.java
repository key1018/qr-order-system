package com.project.qr_order_system.dto.store;

import com.project.qr_order_system.model.StoreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매장 생성 요청 (관리자용)
 */
@Getter
@NoArgsConstructor
public class StoreCreateRequestDto {
    @NotBlank(message = "매장명은 필수입니다.")
    private String storeName;
    @NotNull(message = "매장타입은 필수입니다.")
    private StoreType storeType;
}
