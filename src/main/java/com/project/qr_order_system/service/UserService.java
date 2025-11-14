package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.user.UserLoginRequestDto;
import com.project.qr_order_system.dto.user.UserLoginResponseDto;
import com.project.qr_order_system.dto.user.UserSignRequestDto;
import com.project.qr_order_system.model.Role;
import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.UserRepository;
import com.project.qr_order_system.security.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenPovider;

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

    /**
     * 로그인
     */
    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        UserEntity user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(()-> new IllegalStateException("가입되지 않은 이메일입니다."));

        // 비밀번호 비교
        if(!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // jwt 토큰 생성
        String accessToken = jwtTokenPovider.createNewAccessToken(user);
        String refreshToken = jwtTokenPovider.refreshAccessToken(user);
        return new UserLoginResponseDto(accessToken, refreshToken);
    }

}
