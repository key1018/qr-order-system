package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByStoreId(Long storeId);
    List<ProductEntity> findByStoreIdAndAvailable(Long storeId, String available);
}
