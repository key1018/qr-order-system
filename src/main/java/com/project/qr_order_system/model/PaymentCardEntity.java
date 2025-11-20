package com.project.qr_order_system.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "paymentCard")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCardEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String cardToken;

    @Column(nullable = false)
    private String cardName;

    private boolean isDefault; // 기본카드 사용 여부
}
