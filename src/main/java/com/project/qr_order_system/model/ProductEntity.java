package com.project.qr_order_system.model;

import com.project.qr_order_system.exception.OutOfStockException;
import com.project.qr_order_system.model.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id; // 상품id

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stock;

    private String imageUrl; // 상품 사진 URL

    private String available; // 상품 판매 유무 (판매함 = 'Y' , 판매안함 = 'N')

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="store_id")
    private StoreEntity store;

    public void removeStock(Integer quantity) {
        int restStock = this.stock - quantity;
        if(restStock < 0) {
            throw new OutOfStockException("재고가 부족합니다. (상품명 : "
                    + this.productName + " , 현재 재고 : " + this.stock + ")");
        }
        this.stock = restStock;
    }

    public void restoreStock(Integer quantity) {
        int restStock = this.stock + quantity;
        this.stock = restStock;
    }
}
