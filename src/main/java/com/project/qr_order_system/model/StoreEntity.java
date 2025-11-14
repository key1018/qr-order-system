package com.project.qr_order_system.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 매장 id

    @Column(nullable = false)
    private String storeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StoreType storeType;

    @OneToMany(mappedBy = "store")
    private List<ProductEntity> products = new ArrayList<>();

    // 사장님-매장 연결 -> N:1 관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id") // DB에 'owner_id' (FK) 컬럼이 생성됩니다.
    private UserEntity owner;
}
