package com.project.qr_order_system.persistence;

import com.project.qr_order_system.model.ProductEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findByStoreId(Long storeId);
    List<ProductEntity> findByStoreIdAndAvailable(Long storeId, String available);

    /**
     * 비관적 락 (PESSIMISTIC_WRITE)
     * 먼저 들어온 데이터 수정 후 다음 데이터 조회 가능
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.id = :productId")
    Optional<ProductEntity> findByProductIdWithLock(@Param("productId") Long productId);
}
