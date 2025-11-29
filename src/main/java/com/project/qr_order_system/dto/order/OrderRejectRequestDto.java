package com.project.qr_order_system.dto.order;

import com.project.qr_order_system.model.RejectReason;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderRejectRequestDto {
    private RejectReason reason;
}
