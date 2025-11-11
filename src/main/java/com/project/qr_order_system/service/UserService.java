package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.user.UserLoginRequestDto;
import com.project.qr_order_system.dto.user.UserLoginResponseDto;
import com.project.qr_order_system.dto.user.UserSignRequestDto;
import com.project.qr_order_system.model.Role;
import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor // final 필드 생성자 자동 생성
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public void signup(UserSignRequestDto requestDto) {

        // 비밀번호 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

        UserEntity user = UserEntity.builder()
                .email(requestDto.getEmail())
                .password(password)
                .name(requestDto.getName())
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
    }

}
