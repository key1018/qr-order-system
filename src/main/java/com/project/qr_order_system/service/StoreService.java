package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.store.StoreCreateRequestDto;
import com.project.qr_order_system.dto.store.StoreResponseDto;
import com.project.qr_order_system.model.Role;
import com.project.qr_order_system.model.StoreEntity;
import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.StoreRepository;
import com.project.qr_order_system.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Store;
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
     */
    @Transactional
    public StoreResponseDto createStore(StoreCreateRequestDto requestDto, String email) {

        UserEntity admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if(admin.getRole() == Role.ROLE_ADMIN) {

            StoreEntity storeEntity = StoreEntity.builder()
                    .storeName(requestDto.getStoreName())
                    .storeType(requestDto.getStoreType())
                    .owner(admin)
                    .build();

            StoreEntity savedStore = storeRepository.save(storeEntity);

            return StoreResponseDto.builder()
                    .id(savedStore.getId())
                    .storeName(savedStore.getStoreName())
                    .storeType(savedStore.getStoreType())
                    .build();
        } else {
            throw new IllegalArgumentException("매장을 생성할 수 없습니다.");
        }
    }
}
