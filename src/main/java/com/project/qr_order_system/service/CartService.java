package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.cart.CartAddRequestDto;
import com.project.qr_order_system.dto.cart.CartAddResponseDto;
import com.project.qr_order_system.model.ProductEntity;
import com.project.qr_order_system.persistence.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Redis key 생성 헬더
    private String getCartKey(String email){
        return "cart:" + email;
    }

    /**
     * 장바구니 담기
     */
    @Transactional(readOnly = true)
    public void addItemToCart(CartAddRequestDto requestDto, String email){
        String key = getCartKey(email);
        HashOperations<String, String, CartAddResponseDto> hashOps = redisTemplate.opsForHash();
        String productIdStr = String.valueOf(requestDto.getProductId());

        ProductEntity product = productRepository.findByProductIdWithLock(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 새로운 상품이 이미 담겨있는 가게의 상품인지 확인
        Long newStoreId = product.getStore().getId();

        List<CartAddResponseDto> currentCartItems = hashOps.values(key);

        if (!currentCartItems.isEmpty()) {
            // 장바구니에 담긴 첫 번째 상품의 가게 ID 확인
            CartAddResponseDto firstItem = currentCartItems.get(0);

            // 이미 담겨있는 가게와 같은지 비교
            if (!newStoreId.equals(firstItem.getStoreId())) {
                throw new IllegalArgumentException("동일 가게의 상품만 담을 수 있습니다.");
            }
        }
        if(hashOps.hasKey(key,productIdStr)){
            // 이미 장바구니 있는 상품인지 확인
            CartAddResponseDto existingItem = hashOps.get(key,productIdStr);

            Integer newQuantity = existingItem.getQuantity() + requestDto.getQuantity();

            CartAddResponseDto updateCart = CartAddResponseDto.builder()
                            .storeId(existingItem.getStoreId())
                            .storeName(existingItem.getStoreName())
                            .productId(existingItem.getProductId())
                            .productName(existingItem.getProductName())
                            .quantity(newQuantity)
                            .price(existingItem.getPrice())
                            .totalPrice(newQuantity * existingItem.getPrice())
                            .build();

            hashOps.put(key, productIdStr, updateCart);

        } else {
            // 없으면 새로 추가
            CartAddResponseDto newCart = CartAddResponseDto.builder()
                                .storeId(product.getStore().getId())
                                .storeName(product.getStore().getStoreName())
                                .productId(product.getId())
                                .productName(product.getProductName())
                                .quantity(requestDto.getQuantity())
                                .price(product.getPrice())
                                .totalPrice(product.getPrice() * requestDto.getQuantity())
                                .build();

            hashOps.put(key, productIdStr, newCart);
        }

        // 장바구니 유효시간 설정 (1 시간 뒤 자동 삭제)
        redisTemplate.expire(key, 1, TimeUnit.HOURS);

        log.info("장바구니 추가 완료: 사용자={}, 상품={}, 수량={}", email, requestDto.getProductId(), requestDto.getQuantity());
    }

    /**
     * 장바구니 조회
     */
    public List<CartAddResponseDto> getCartList(String email){

        String key = getCartKey(email);
        HashOperations<String, String, CartAddResponseDto> hashOps = redisTemplate.opsForHash();

        // Redis Map에서 Values(DTO 리스트)만 뽑아냄
        Map<String, CartAddResponseDto> cartMap = hashOps.entries(key);
        return new ArrayList<>(cartMap.values());
    }

    /**
     * 장바구니 수량 변경 (증감 로직)
     * - quantity가 양수면 증가 (+1), 음수면 감소 (-1)
     * - 계산된 최종 수량이 0 이하가 되면 에러 발생
     */
    public void updateCartItemQuantity(CartAddRequestDto requestDto, String email){
        String key = getCartKey(email);
        HashOperations<String, String, CartAddResponseDto> hashOps = redisTemplate.opsForHash();
        String productIdStr = String.valueOf(requestDto.getProductId());

        if(!hashOps.hasKey(key,productIdStr)){
            throw new IllegalArgumentException("해당 상품이 존재하지 않습니다.");
        }

        // 기존 장바구니 목록 가져오기
        CartAddResponseDto existingItem = hashOps.get(key,productIdStr);

        Integer newQuantity = existingItem.getQuantity() + requestDto.getQuantity();

        if(newQuantity <= 0){
            throw new IllegalArgumentException("최소 1개 이상이어야 합니다.");
        }

        CartAddResponseDto updateCart = CartAddResponseDto.builder()
                .storeId(existingItem.getStoreId())
                .storeName(existingItem.getStoreName())
                .productId(existingItem.getProductId())
                .productName(existingItem.getProductName())
                .quantity(newQuantity)
                .price(existingItem.getPrice())
                .totalPrice(existingItem.getPrice() * newQuantity)
                .build();

        hashOps.put(key, productIdStr, updateCart);
    }

    /**
     * 장바구니 상품 삭제
     */
    public void deleteCart(Long productId, String email){
        String key = getCartKey(email);
        redisTemplate.opsForHash().delete(key, String.valueOf(productId));
    }

    /**
     * 장바구니 비우기
     * => 주문 완료 후 비우기
     */
    public void clearCart(String email){
        String key = getCartKey(email);
        redisTemplate.delete(key);
    }
}
