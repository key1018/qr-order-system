package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.product.ProductRequestDto;
import com.project.qr_order_system.dto.product.ProductResponseDto;
import com.project.qr_order_system.model.ProductEntity;
import com.project.qr_order_system.model.Role;
import com.project.qr_order_system.model.StoreEntity;
import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.ProductRepository;
import com.project.qr_order_system.persistence.StoreRepository;
import com.project.qr_order_system.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    /**
     * 매장 상품 등록
     */
    @Transactional
    public List<ProductResponseDto> createProduct(List<ProductRequestDto> requestDtoList, Long storeId, String email) {

        // 관리자 확인
        UserEntity admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("관리자를 찾을 수 없습니다."));

        if(admin.getRole() != Role.ROLE_ADMIN){
            throw new SecurityException("관리자가 아닙니다.");
        }

        // 매장 확인
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        List<ProductEntity> productEntities = requestDtoList.stream()
                .map(requestDto -> ProductEntity.builder()
                        .productName(requestDto.getProductName())
                        .price(requestDto.getPrice())
                        .stock(requestDto.getStock())
                        .imageUrl(requestDto.getImageUrl())
                        .available(requestDto.getAvailable())
                        .store(store)
                        .build())
                .collect(Collectors.toList());

        List<ProductEntity> savedProducts = productRepository.saveAll(productEntities);

        return savedProducts.stream()
                .map(savedProduct -> ProductResponseDto.builder()
                        .id(savedProduct.getId())
                        .productName(savedProduct.getProductName())
                        .price(savedProduct.getPrice())
                        .stock(savedProduct.getStock())
                        .imageUrl(savedProduct.getImageUrl())
                        .available(savedProduct.getAvailable())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 상품 조회 (관리자용)
     * available in ('Y','N')
     */
    @Transactional
    public List<ProductResponseDto> retrieveProductListForAdmin(Long storeId, String email) {
        // 매장 확인
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입한 고객이 아닙니다."));

        if(user.getRole() != Role.ROLE_ADMIN){
            throw new SecurityException("관리자가 아닙니다.");
        }

        List<ProductEntity> productEntities = productRepository.findByStoreId(storeId);

        return productEntities.stream()
                .map(productList -> ProductResponseDto.builder()
                        .id(productList.getId())
                        .productName(productList.getProductName())
                        .price(productList.getPrice())
                        .stock(productList.getStock())
                        .imageUrl(productList.getImageUrl())
                        .available(productList.getAvailable())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 상품 조회 (고객용)
     * available = 'Y'
     */
    @Transactional
    public List<ProductResponseDto> retrieveProductListForUser(Long storeId, String email, String available) {
        // 매장 확인
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입한 고객이 아닙니다."));

        List<ProductEntity> productEntities = productRepository.findByStoreIdAndAvailable(storeId, available);

        return productEntities.stream()
                .map(productList -> ProductResponseDto.builder()
                        .id(productList.getId())
                        .productName(productList.getProductName())
                        .price(productList.getPrice())
                        .imageUrl(productList.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 상품 재고 감소
     */
    @Transactional
    public void removeStock(Long storeId, Long productId, Integer quantity) {
        // 매장 확인
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 업습니다."));

        // 상품 확인
        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 업습니다."));

        if (!product.getStore().getId().equals(storeId)) {
            throw new IllegalArgumentException("해당 상품은 이 매장의 상품이 아닙니다.");
        }

        product.removeStock(quantity);
    }
}
