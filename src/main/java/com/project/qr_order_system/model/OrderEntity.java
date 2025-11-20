package com.project.qr_order_system.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Setter
@Getter
@Entity
@Table(name = "orders") // db에서 order by 인줄 알아서 변경
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 주문번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false)
    private String usedCardToken;
    @Column(nullable = false)
    private String usedCardName;

    // takeout이면 null일 수 있음
    private Integer tableNumber;

    @Column(nullable = false)
    private Integer totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status; // 주문 상태

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItemEntity> orderItems = new ArrayList<>();

}
