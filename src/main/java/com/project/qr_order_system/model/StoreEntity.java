package com.project.qr_order_system.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "store")
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
}
