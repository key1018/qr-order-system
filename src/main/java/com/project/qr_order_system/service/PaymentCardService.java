package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.payment.PaymentCardRegisterRequestDto;
import com.project.qr_order_system.dto.payment.PaymentCardResponseDto;
import com.project.qr_order_system.model.PaymentCardEntity;
import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.PaymentCardRepository;
import com.project.qr_order_system.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    /**
     * 카드 등록 (임시)
     */
    @Transactional
    public PaymentCardResponseDto registerCard(PaymentCardRegisterRequestDto requestDto, String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 임시 카드 생성
        String tmpCard = "CARD_" + UUID.randomUUID().toString();

        PaymentCardEntity paymentCardEntity = PaymentCardEntity.builder()
                .cardName(requestDto.getCardName())
//                .cardToken(requestDto.getCardToken())
                .cardToken(tmpCard)
                .isDefault(true)
                .user(user)
                .build();

        PaymentCardEntity savedCard = paymentCardRepository.save(paymentCardEntity);

        return PaymentCardResponseDto.builder()
                .id(savedCard.getId())
                .cardName(savedCard.getCardName())
                .isDefault(savedCard.isDefault())
                .build();
    }

}
