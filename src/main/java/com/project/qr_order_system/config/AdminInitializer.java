package com.project.qr_order_system.config;

import com.project.qr_order_system.model.Role;
import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    // CommandLineRunner : Spring boot가 시작될 떄 특정 로직을 실행하게 해줌

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Valid
    private String adminId  = "admin@qrorder.com";
    private String adminPassword = "admin123!";

    @Override
    public void run(String... args) throws Exception {
        userRepository.findByEmail(adminId)
                .ifPresentOrElse(
                        // 이미 존재하는 경우
                        user -> log.info("관리자 계정(admin@qrorder.com)이 이미 존재합니다."),
                        // 존재하지 않는 경우
                        () -> {
                            log.info("관리자 계정을 생성합니다.");
                            UserEntity admin = UserEntity.builder()
                                    .email(adminId)
                                    .password(passwordEncoder.encode(adminPassword))
                                    .name("관리자")
                                    .role(Role.ROLE_ADMIN)
                                    .build();

                            userRepository.save(admin);
                            log.info("관리자 계정을 생성을 완료했습니다.");
                        }
                );
    }
}
