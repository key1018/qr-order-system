package com.project.qr_order_system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 요청이 들어오면 JWT 토큰이 있는지 확인하고, 인증된 사용자로 등록할지 결정

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    private static final String HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws IOException, ServletException {

        try {
            // 1. 요청 헤더에서 토큰 꺼내기
            String token = resolveToken(request);

            // 2. 토큰 검증
            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
                // 3. 토큰이 유효할 경우, 토큰에서 이메일(사용자 식별 정보) 꺼내기
                String email = tokenProvider.getEmailFromToken(token);

                // 4. 이메일로 DB에서 실제 사용자 정보(UserDetails) 조회
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                // 5. (가장 중요) 인증된 사용자라는 '티켓' 생성
                //    (사용자 정보, null, 권한)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 6. Spring Security의 '보관함'에 이 '티켓'을 저장
                //    이제 이 요청은 '인증된 요청'으로 처리됨
                // 즉, SecurityContextHolder에 인증 정보를 저장하면 Spring Security가 이 사용자를 인증된 사용자로 인식
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Authenticated user: {}, setting security context", email);
            }
        } catch (Exception e) {
            log.error("Error during authentication in security context: {}", e.getMessage());
        }

        // 7. 다음 필터로 요청/응답 전달
        filterChain.doFilter(request, response);
    }

    // HTTP Request 헤더에서 'Authorization' 값을 가져와 'Bearer ' 부분을 잘라내고 토큰만 반환
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }
        return null;
    }
}
