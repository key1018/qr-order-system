package com.project.qr_order_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing // 자동으로 시간 기록하는 기능
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 로그인이 안 된 상태거나(null), 인증되지 않은 경우
            if(authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty(); // null 로 저장
            }

            // 로그인한 사람의 이름 반환
            return Optional.ofNullable(authentication.getName());
        };
    }
}
