package com.project.qr_order_system.service;

import com.project.qr_order_system.controller.OrderController;
import com.project.qr_order_system.dto.order.OrderResponseDto;
import com.project.qr_order_system.dto.order.OrderStatusUpdateDto;
import com.project.qr_order_system.model.OrderEntity;
import com.project.qr_order_system.model.Role;
import com.project.qr_order_system.model.StoreEntity;
import com.project.qr_order_system.model.UserEntity;
import com.project.qr_order_system.persistence.OrderRepository;
import com.project.qr_order_system.persistence.StoreRepository;
import com.project.qr_order_system.persistence.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@AllArgsConstructor
public class NotificationService {

    // SseEmitter : 서버가 발생하는 이벤트를 즉시 사용자에게 보내도록 도와주는 Spring의 SSE 전송용 객체
    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>(); // 가게용
    private final Map<Long, SseEmitter> customerEmitterMap = new ConcurrentHashMap<>(); // 고객용
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;

    /**
     * 실시간 주문 알림 전송 받기 (관리자용)
     * 1시간 뒤 만료 -> 클라이언트가 재접속 -> 재접속 시 주문 목록 조회 API 호출(동기화)
     * => 특정 매장의 관리자가 SSE로 실시간 주문 알림을 받을 수 있도록 emitter를 생성·저장하고,
     *    연결 상태를 관리하며, 초기 연결 신호를 보내는 메소드
     */
    public SseEmitter subscribe(Long storeId, String email){

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        UserEntity admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (admin.getRole() != Role.ROLE_ADMIN) {
            throw new SecurityException("관리자 권한이 없습니다.");
        }

        if (!store.getOwner().getEmail().equals(email)) {
            throw new SecurityException("해당 매장의 관리자가 아닙니다.");
        }

        SseEmitter emitter = new SseEmitter(3600000L); // 1시간 (1시간 동안 클라이언트가 연결 유지)
        emitterMap.put(storeId, emitter); // 특정 매장(storeId)을 구독한 SSE 연결을 저장 -> 알림 전송 시 storeId를 찾아서 전송

        // 연결 종료 시 메모리 누수 방지
        emitter.onCompletion(() -> emitterMap.remove(storeId)); // 클라이언트가 정상적으로 연결 종료 시 연결 제거
        emitter.onTimeout(() -> emitterMap.remove(storeId)); // 타임아웃 발생 시 연결 제거
        emitter.onError((e) -> emitterMap.remove(storeId)); // 전송 중 에러 발생 시 연결 제거

        try {
            // 클라이언트가 연결되었다는 신호 전송
            // 연결 성공 시 더미 데이터 전송 (503 방지)
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return emitter;
    }

    /**
     * 주문 알림 전송 (시스템)
     * => 특정 매장(storeId)을 구독 중인 관리자에게 새로운 주문 발생 이벤트를 SSE로 전송하고,
     *    전송 실패 시 해당 연결을 정리하는 메소드
     */
    public void sendOrderAlert(Long storeId, String eventName, OrderResponseDto responseDto){
        SseEmitter emitter = emitterMap.get(storeId);
        if(emitter != null){
            try{
                // "new-order"라는 이름의 이벤트 발송
                emitter.send(SseEmitter.event().name(eventName).data(responseDto));
            }catch (IOException e){
                emitterMap.remove(storeId); // 오류발생 시 연결 제거
                // 알림 전송 실패 -> 관리자가 주문조회API로 확인해야됨
                log.warn("알림 전송 실패 : {}", storeId);
            }
        }
    }


    /**
     * 실시간 주문 결과 전송 받기 (고객용)
     * 1시간 뒤 만료 -> 클라이언트가 재접속 -> 재접속 시 주문 목록 조회 API 호출(동기화)
     * => 특정 매장의 고객이 SSE로 실시간 주문 알림을 받을 수 있도록 emitter를 생성·저장하고,
     *    연결 상태를 관리하며, 초기 연결 신호를 보내는 메소드
     */
    public SseEmitter customerSubscribe(Long storeId, Long orderId, String email){

        StoreEntity store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 내역을 찾을 수 없습니다."));

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if(!order.getUser().getId().equals(user.getId())){
            throw new SecurityException("본인의 주문 내역만 확인할 수 있습니다.");
        }

        if(!order.getStore().getId().equals(store.getId())){
            throw new SecurityException("요청하신 매장의 주문이 아닙니다.");
        }

        SseEmitter emitter = new SseEmitter(3600000L); // 1시간 (1시간 동안 클라이언트가 연결 유지)
        customerEmitterMap.put(orderId, emitter); // 특정 매장(storeId)을 구독한 SSE 연결을 저장 -> 알림 전송 시 storeId를 찾아서 전송

        // 연결 종료 시 메모리 누수 방지
        emitter.onCompletion(() -> customerEmitterMap.remove(orderId)); // 클라이언트가 정상적으로 연결 종료 시 연결 제거
        emitter.onTimeout(() -> customerEmitterMap.remove(orderId)); // 타임아웃 발생 시 연결 제거
        emitter.onError((e) -> customerEmitterMap.remove(orderId)); // 전송 중 에러 발생 시 연결 제거

        try {
            // 클라이언트가 연결되었다는 신호 전송
            // 연결 성공 시 더미 데이터 전송 (503 방지)
            emitter.send(SseEmitter.event().name("connect").data("connected!"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return emitter;
    }

    /**
     * 주문 알림 전송 (시스템)
     * => 특정 매장(storeId)을 구독 중인 관리자에게 새로운 주문 발생 이벤트를 SSE로 전송하고,
     *    전송 실패 시 해당 연결을 정리하는 메소드
     */
    public void sendCustomerOrderAlert(Long orderId, OrderStatusUpdateDto responseDto){
        SseEmitter emitter = customerEmitterMap.get(orderId);
        if(emitter != null){
            try{
                // "new-order"라는 이름의 이벤트 발송
                emitter.send(SseEmitter.event().name("update-order").data(responseDto));
            }catch (IOException e){
                customerEmitterMap.remove(orderId); // 오류발생 시 연결 제거
                // 알림 전송 실패 -> 고객이 주문조회API로 확인해야됨
                log.warn("알림 전송 실패 : {}", orderId);
            }
        }
    }
}
