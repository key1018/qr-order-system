package com.project.qr_order_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // api를 통해서 로그인하기 때문에 비활성화
                .httpBasic(AbstractHttpConfigurer::disable) // JWT 토큰을 헤더에 실어 보내는 'Bearer' 방식을 사용하기 때문에 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session 기반이 아님을 선언 (JWT 사용)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/qrorder/users/signup","/qrorder/users/login").permitAll() // 회원가입, 로그인은 인증 없이 가능
                        .requestMatchers("/qrorder/admin/**").hasRole("ADMIN")
                        .requestMatchers("/qrorder/**").hasRole("USER") // 나머지는 로그인 필수
                        .anyRequest().permitAll()
                )
                .build();
    }

}
