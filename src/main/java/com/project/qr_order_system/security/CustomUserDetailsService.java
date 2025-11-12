package com.project.qr_order_system.security;

import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // DB에서 사용자 정보 조회
        return userRepository.findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(() -> new IllegalArgumentException(email + "를 DB에서 확인 불가능"));
    }

    /**
     * DB의 UserEntity를 Spring Security가 사용하는 UserDetails 객체로 변환
     */
    private UserDetails createUserDetails(UserEntity userEntity) {
        return User.builder()
                .username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(userEntity.getRole().name())
                .build();
    }
}
