package com.project.qr_order_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 자바와 redis 연결
        // 기본 포트 : 6379
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // DB의 저장소처럼 Redis를 사용
        // RedisTemplate: Redis에 데이터를 넣고 빼는 도구
        // ConnectionFactory : Redis 위치(주소, 포트)를 알려줌
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // key는 String으로 저장 (EX : "cart : user.qrorder.com")
        // Serializer : 직렬화 설정 (사람이 읽을 수 있는 문자열로 저장)
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // Value는 json으로 저장 (DTO객체를 JSON으로 변환)
        // GenericJackson2JsonRedisSerializer :
        // 자바 객체(Object)를 Redis에 저장(객체의 클래스 타입 정보를 JSON 안에 같이 저장)할 때
        // JSON 형식으로 변환(직렬화)해주고
        // 불러올 때 다시 자바 객체로 변환(역직렬화)해주는 역할
        //{
        //  "@class": "com.project.qr_order_system.dto.cart.CartItemDto",
        //  "productId": 1,
        //  "quantity": 2
        //}
        // @class라는 필드 어떤 DTO인지 적어두었기 때문에 알아서 원본 객체로 복구
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }
}
