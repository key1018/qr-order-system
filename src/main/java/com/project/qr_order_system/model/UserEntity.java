package com.project.qr_order_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "users") // db에서 이미 user 현재 사용한 접속자를 가르켜서 변경
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ROLE_USER(사용자), ROLE_ADMIN(매장 사장/직원)

    // --- 1:N 관계 추가 ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentCardEntity> paymentCards = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<OrderEntity> orders = new ArrayList<>();

    // 사장님-매장 연결 -> N:1 관계 추가
    // 한 명의 사장님(User)이 여러 개의 매장(Store)을 소유
    @OneToMany(mappedBy = "owner")
    private List<StoreEntity> ownedStores = new ArrayList<>();

//    @Builder
//    public UserEntity(Long id, String email, String password, String name, Role role, List<PaymentCardEntity> paymentCards, List<OrderEntity> orders) {
//        this.id = id;
//        this.email = email;
//        this.password = password;
//        this.name = name;
//        this.role = role;
//        this.paymentCards = paymentCards;
//        this.orders = orders;
//    }
}


