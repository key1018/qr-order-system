package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.store.StoreCreateRequestDto;
import com.project.qr_order_system.dto.store.StoreResponseDto;
import com.project.qr_order_system.model.StoreEntity;
import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.StoreRepository;
import com.project.qr_order_system.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    /**
     * 매장 생성
     * 역할 체크는 @PreAuthorize로 처리되므로 역할 체크 로직 제거
     */
    @Transactional
    public StoreResponseDto createStore(StoreCreateRequestDto requestDto, String email) {

        UserEntity admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        StoreEntity storeEntity = StoreEntity.builder()
                .storeName(requestDto.getStoreName())
                .storeType(requestDto.getStoreType())
                .storeImage(requestDto.getStoreImage())
                .owner(admin)
                .build();

        StoreEntity savedStore = storeRepository.save(storeEntity);

        return StoreResponseDto.builder()
                .id(savedStore.getId())
                .storeName(savedStore.getStoreName())
                .storeType(savedStore.getStoreType())
                .build();
    }

    /**
     * 매장 소유자 확인용 메서드
     * 역할 체크는 @PreAuthorize로 처리되므로 매장 소유자 체크만 수행
     */
    @Transactional
    public void validateStoreOwner(String email, Long storeId){
        // 매장 정보 조회
        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalThreadStateException("매장 정보가 없습니다."));

        if(store.getOwner() == null){
            throw new IllegalArgumentException("매장 소유주가 없습니다.");
        }

        if(!store.getOwner().getEmail().equals(email)){
            throw new SecurityException("매장에 대한 접근 권한이 없습니다.");
        }

        log.info("매장 소유주 확인: user={}, storeId={}", email, storeId);
    }


}
