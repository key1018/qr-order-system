package com.project.qr_order_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.qr_order_system.dto.kafka.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 설정 클래스
 * Producer와 Consumer의 직렬화/역직렬화 설정
 * 
 * LocalDateTime 직렬화/역직렬화를 위해 Spring Boot가 관리하는 ObjectMapper를 주입받아 사용
 */
@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfig {
    
    private final ObjectMapper objectMapper;
    
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;
    
    /**
     * Producer Factory 설정
     * JsonSerializer를 사용하여 OrderEvent 객체를 JSON으로 직렬화
     * ObjectMapper를 주입받아 LocalDateTime 등 Java 8 날짜 타입을 올바르게 처리
     */
    @Bean
    public ProducerFactory<String, OrderEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        
        // ObjectMapper를 사용하는 JsonSerializer 인스턴스 생성
        // LocalDateTime 등 Java 8 날짜 타입을 올바르게 직렬화
        JsonSerializer<OrderEvent> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setAddTypeInfo(false); // 타입 정보 헤더 추가 안 함
        
        // Serializer 인스턴스를 직접 전달하여 설정
        return new DefaultKafkaProducerFactory<>(config, new StringSerializer(), jsonSerializer);
    }
    
    /**
     * KafkaTemplate 빈 등록
     */
    @Bean
    public KafkaTemplate<String, OrderEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    /**
     * Consumer Factory 설정
     * JsonDeserializer를 사용하여 JSON을 OrderEvent 객체로 역직렬화
     * ObjectMapper를 주입받아 LocalDateTime 등 Java 8 날짜 타입을 올바르게 처리
     * 
     * 주의: group-id는 application.properties에서 설정됨
     * 각 서비스 개발자가 자신의 서비스에 맞게 변경해야 함
     */
    @Bean
    public ConsumerFactory<String, OrderEvent> consumerFactory(
            @Value("${spring.kafka.consumer.group-id}") String groupId
    ) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        
        // 자동 커밋 비활성화 (수동 Ack를 위해 필수)
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        
        // ObjectMapper를 사용하는 JsonDeserializer 인스턴스 생성
        // LocalDateTime 등 Java 8 날짜 타입을 올바르게 역직렬화
        // OrderEvent.class를 명시적으로 지정했으므로 타입 정보 헤더가 없어도 역직렬화 가능
        JsonDeserializer<OrderEvent> jsonDeserializer = new JsonDeserializer<>(OrderEvent.class, objectMapper);
        jsonDeserializer.addTrustedPackages("*"); // 신뢰할 수 있는 패키지 설정
        
        // Deserializer 인스턴스를 직접 전달하여 설정
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), jsonDeserializer);
    }
    
    /**
     * Kafka Listener Container Factory 설정
     * 수동 Ack 모드 설정 (MANUAL_IMMEDIATE)
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, OrderEvent> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, OrderEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        
        // 수동 Ack 모드 설정 (MANUAL_IMMEDIATE)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        return factory;
    }
}

