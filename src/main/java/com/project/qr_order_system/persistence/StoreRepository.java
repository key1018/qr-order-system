package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;


public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
//    Optional<StoreEntity> findByStoreIdAndStoreName(Long storeId, String storeName);
}
