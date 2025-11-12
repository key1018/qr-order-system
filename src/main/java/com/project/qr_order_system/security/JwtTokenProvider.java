package com.project.qr_order_system.security;

import com.project.qr_order_system.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secret;

    private SecretKey secretKey;

    @Value("${jwt.expire-time.access}")
    private Long expireTime;

    @Value("${jwt.expire-time.refresh}")
    private Long refreshExpireTime;

    private static final String AUTH_KEY = "userId";
    private static final String ISSUER = "qr-order";

    @PostConstruct
    public void initSecretKey() {
        log.debug("Raw SecretKey: {}", secret);
        if(secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("Secret key is empty");
        }
        try{
            // Decoder.BASE64.decode() 메서드는 Base64 형식으로 인코딩 된 Secret Key를 디코딩한 후, byte array를 반환
            byte[] decodeKey = Decoders.BASE64.decode(secret);
            if(decodeKey == null || decodeKey.length == 0) {
                throw new IllegalArgumentException("Secret key is invalid");
            }
            if(decodeKey.length != 32) {
                throw new IllegalArgumentException("JWT secret key must be 256-bit (32 bytes) after Base64 decoding");
            }
            // Keys.hmacShaKeyFor() 메서드는 key byte array를 기반으로 적절한 HMAC 알고리즘을 적용한 Key(java.security.Key) 객체를 생성
            this.secretKey = Keys.hmacShaKeyFor(decodeKey);
            log.debug("Secret key initialized successfully: {}", secret);
        } catch (IllegalArgumentException e) {
            log.error("Invalid JWT secret key: Ensure it is Base64 encoded and 256-bit long", e);
            throw e;
        }
    }

    /**
     * Access token 생성
     * @param userEntity
     * @return header.payload.signature
     * 최종 예시 : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9. // header    {"alg": "HS256","typ": "JWT"} base64로 인코딩한 깂
     * eyJzdWIiOiJldW55b3VuZy5raW1AZXhhbXBsZS5jb20iLCJyb2xlIjoiQURNSU4ifQ. // payload
     * TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ // signature : (encodedHeader + "." + encodedPayload) 를 Base64 디코딩된 secretKey로 HMAC-SHA256 해시 → base64로 인코딩한 값
     */
    public String createNewAccessToken(final UserEntity userEntity) {

        // userEntity 예시
//        userEntity = {
//                id: 1001,
//                email: "test@example.com",
//                name: "홍길동",
//                role: Role.USER
//        }

        Instant now = Instant.now();
        Date nowData = Date.from(now);
        Date expiration = Date.from(now.plusMillis(expireTime));

        Claims claims = Jwts.claims().setSubject(userEntity.getEmail());
        claims.put("role", userEntity.getRole().toString());

        // 최종 payload 예시 => base64로 인코딩한 값 (eyJzdWIiOiJldW55b3VuZy5raW1AZXhhbXBsZS5jb20iLCJyb2xlIjoiQURNSU4ifQ)
//        {
//            "sub": "test@example.com",
//            "iss": "qr-order",
//            "iat": 1731449110,
//            "nbf": 1731449110,
//            "exp": 1731535510,
//            "authKey": 1001,
//            "role": "USER",
//            "userName": "홍길동"
//        }

        return Jwts.builder()
                .setSubject(userEntity.getEmail()) // 주체 (이메일 : test@example.com)
                .setIssuer(ISSUER) // 발급자 (qr-order)
                .setIssuedAt(nowData) // 발급시간 (현재시간)
                .setNotBefore(nowData) // 현재시간 이후부터 유효
                .setExpiration(expiration) // 만료 시간
                // ---- 사용자 정의 정보들 (payload) ----
                .claim(AUTH_KEY,userEntity.getId()) // 사용자ID ( "authKey" : 1001 )
                .claim("role", userEntity.getRole().name()) // 역할 ( "role" : "USER" )
                .claim("userName", userEntity.getName()) // 사용자이름 ( "username" : "홍길동" )
                .signWith(secretKey) // 서명 키 (Base64로 인코딩된 256비트 키)
                .compact(); // 토큰 생성
    }

    /**
     * Access token 생성
     * @param userEntity
     * @return
     */
    public String refreshAccessToken(final UserEntity userEntity) {

        Instant now = Instant.now();
        Date nowData = Date.from(now);
        Date expiration = Date.from(now.plusMillis(refreshExpireTime));

        Claims claims = Jwts.claims().setSubject(userEntity.getEmail());
        claims.put("role", userEntity.getRole().toString());

        return Jwts.builder()
                .setSubject(userEntity.getEmail()) // 주체 (이메일 : test@example.com)
                .setIssuedAt(nowData) // 발급시간 (현재시간)
                .setNotBefore(nowData) // 현재시간 이후부터 유효
                .setExpiration(expiration) // 만료 시간
                .signWith(secretKey) // 서명 키 (Base64로 인코딩된 256비트 키)
                .compact(); // 토큰 생성
    }

    /**
     * 토큰에서 이메일 추출
     * 이미 발급된 JWT 토큰을 읽고 검증하는 단계
     * => 누구의 토큰/만료된 토큰인지/진짜인지 추출하는 단계
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token) // 전달받은 토큰을 Header, Payload, Signature를 분리해서 다시 계산
                .getBody(); // 검증 통과 시 JSON값으로 가져옴
        return claims.getSubject(); // 이메일 반환
    }

    /**
     * 토큰 유효성 검증
     * 전달받은 토큰이 위조/만료/형식이 모두 올바른지 검증하는 단계
     */
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.warn("유효하지 않은 JWT 토큰 : {}", e.getMessage());
            return false;
        }
    }
}
