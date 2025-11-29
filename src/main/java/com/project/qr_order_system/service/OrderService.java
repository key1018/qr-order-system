package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.order.OrderItemResponseDto;
import com.project.qr_order_system.dto.order.OrderRequestDto;
import com.project.qr_order_system.dto.order.OrderResponseDto;
import com.project.qr_order_system.dto.order.OrderStatusUpdateDto;
import com.project.qr_order_system.model.*;
import com.project.qr_order_system.persistence.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    /**
     * 주문 등록 (고객용)
     */
    @Transactional
    public OrderResponseDto addOrder(OrderRequestDto orderRequestDto, String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        StoreEntity store = storeRepository.findById(orderRequestDto.getStoreId())
                .orElseThrow(() -> new IllegalArgumentException("매장을 찾을 수 없습니다."));

        PaymentCardEntity card = paymentCardRepository.findById(orderRequestDto.getPaymentCardId())
                .orElseThrow(() -> new IllegalArgumentException("등록된 카드가 없습니다."));

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .store(store)
                .usedCardToken(card.getCardToken())
                .usedCardName(card.getCardName())
                .tableNumber(orderRequestDto.getTableNumber())
                .status(OrderStatus.ORDERED)
                .build();

        int totalPrice = 0;
        List<OrderItemEntity> orderItems = new ArrayList<>();

        for(var itemDto : orderRequestDto.getOrderItems()) {
            ProductEntity product = productRepository.findByProductIdWithLock(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            // 재고 감소 (주문 승낙 시 재고 감소하는것으로 변경)
//            product.removeStock(itemDto.getQuantity());

            // 주문 상세 설정
            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.getQuantity())
                    .orderPrice(product.getPrice() * itemDto.getQuantity())
                    .build();

            orderItems.add(orderItem);
            totalPrice += orderItem.getOrderPrice();
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        OrderEntity savedOrder = orderRepository.save(order);

        OrderResponseDto responseDto = getOrderResponseDto(savedOrder);

        // 관리자에게 실시간 알림 전송
        notificationService.sendOrderAlert(store.getId(),"new-order", responseDto);

        return responseDto;
    }

    /**
     * 주문 승낙 (관리자용)
     */
    @Transactional
    public OrderResponseDto acceptOrder(Long storeId, Long orderId, String email) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 내역이 없습니다."));

        validateStoreOwner(storeId, email);

        // 주문 승낙 (주문 상태 변경)
        order.setStatus(OrderStatus.IN_PROGRESS);

        // 재고 감소 (주문 승낙시)
        for(OrderItemEntity orderItem : order.getOrderItems()) {
            ProductEntity product = productRepository.findByProductIdWithLock(orderItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다"));

            product.removeStock(orderItem.getQuantity());
        }

        OrderResponseDto responseDto = getOrderResponseDto(order);

        // 내 앞 대기 인원수 계산
        long watingP = orderRepository.countByStoreIdAndStatusAndIdLessThan(storeId,order.getStatus(),order.getId());

        // 예상 대기 시간 계산 (한 명당 소요시간 5분으로 가정함)
        int watingM = (int)(watingP + 1) * 5;

        OrderStatusUpdateDto updateDto = OrderStatusUpdateDto.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus())
                .waitingPosition((int)watingP)
                .waitingTime(watingM)
                .build();

        // 고객에게 실시간 알림 전송
        notificationService.sendCustomerOrderAlert(order.getId(), updateDto);

        return getOrderResponseDto(order);
    }

    /**
     * 주문 조리 완료 (관리자용)
     * 고객한테 완료 안내 보내기
     */
    @Transactional
    public OrderResponseDto completeOrder(Long storeId, Long orderId, String email) {

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 내역이 없습니다."));

        validateStoreOwner(storeId, email);

        if(!order.getStatus().equals(OrderStatus.IN_PROGRESS)) {
            throw new IllegalArgumentException("조리중인 주문 건만 완료 가능합니다.");
        }

        // 주문 완료 (주문 상태 변경)
        order.setStatus(OrderStatus.READY);
        OrderResponseDto responseDto = getOrderResponseDto(order);

        // 고객한테 안내 보내기
        OrderStatusUpdateDto completeDto = OrderStatusUpdateDto.builder()
                .orderId(order.getId())
                .orderStatus(order.getStatus())
                .waitingPosition(0) // 대기 없음
                .waitingTime(0) // 대기 없음
                .build();

        // 고객에게 실시간 알림 전송
        notificationService.sendCustomerOrderAlert(order.getId(), completeDto);

        // 뒷사람 대기인원,대기시간 줄이기
        updateBackWaitingPositionAndTime(storeId,email);

        return responseDto;
    }

    /**
     * 뒷사람 대기인원,대기시간 줄이기 (관리자용)
     */
    @Transactional
    public void updateBackWaitingPositionAndTime(Long storeId, String email) {

        validateStoreOwner(storeId, email);

        // STATUS : IN_PROGRESS 중인 것만 조회
        List<OrderEntity> orderLists = orderRepository.findByStoreIdAndStatus(storeId, OrderStatus.IN_PROGRESS,Sort.by(Sort.Direction.ASC,"createdAt"));

        for(OrderEntity orderEntity : orderLists) {
            // 각 주문 별로 내 앞의 인원 수 재계산
            long watingP = orderRepository.countByStoreIdAndStatusAndIdLessThan(storeId,OrderStatus.IN_PROGRESS, orderEntity.getId());

            // 각 주문 별로 내 앞의 대기 시간 재계산
            int watingM = (int)(watingP + 1) * 5;

            // 고객한테 다시 안내 보내기
            OrderStatusUpdateDto updateDto = OrderStatusUpdateDto.builder()
                    .orderId(orderEntity.getId())
                    .orderStatus(orderEntity.getStatus())
                    .waitingPosition((int)watingP)
                    .waitingTime(watingM)
                    .build();

            // 고객에게 실시간 알림 전송
            notificationService.sendCustomerOrderAlert(orderEntity.getId(), updateDto);
        }
    }

    /**
     * 주문 취소 (고객용)
     * 손님의 변심으로 인한 취소
     * 조리 전 (IN_PROGRESS 전) -> 재고 감소 X
     */
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, String email) {

        OrderEntity cancelOrder = validateCustomerOrder(orderId, email);

        if(!cancelOrder.getStatus().equals(OrderStatus.ORDERED)) {
            throw new IllegalArgumentException("이미 접수된 주문은 취소할 수 없습니다. 매장에 문의하세요.");
        }

        cancelOrder.setStatus(OrderStatus.CANCELED);

        OrderResponseDto responseDto = getOrderResponseDto(cancelOrder);

        // 관리자에게 취소 안내 보내기
        notificationService.sendOrderAlert(cancelOrder.getStore().getId(), "cancel-order", responseDto);

        // 고객한테 취소 안내 보내기
        OrderStatusUpdateDto updateDto = OrderStatusUpdateDto.builder()
                .orderId(cancelOrder.getId())
                .orderStatus(cancelOrder.getStatus())
                .waitingPosition(0)
                .waitingTime(0)
                .build();

        // 고객에게 실시간 알림 전송
        notificationService.sendCustomerOrderAlert(cancelOrder.getId(), updateDto);

        log.info("고객 주문 취소 완료: OrderId={}", orderId);

        return responseDto;
    }

    /**
     * 주문 목록 조회 (전체) : 관리자용
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStore(Long storeId, String email) {

        validateStoreOwner(storeId, email);

        List<OrderEntity> orders = orderRepository.findAllByStoreId(storeId
                , Sort.by(Sort.Direction.DESC, "createdAt"));

        return orders.stream()
                .map(this::getOrderResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 주문 목록 조회 (상태별) : 관리자용
     */
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersStatusByStore(Long storeId, String email, OrderStatus status) {

        validateStoreOwner(storeId, email);

        List<OrderEntity> orders = orderRepository.findByStoreIdAndStatus(storeId, status
                , Sort.by(Sort.Direction.DESC, "createdAt"));

        return orders.stream().map(this::getOrderResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 관리자 확인용 메서드
     */
    void validateStoreOwner(Long storeId, String email) {
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
    }

    /**
     * 고객 주문 확인용 메서드
     */
    private OrderEntity validateCustomerOrder(Long orderId, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문 내역을 찾을 수 없습니다."));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("본인의 주문만 취소할 수 있습니다.");
        }

        return order;
    }

    /**
     * 주문 목록 리턴용 메서드
     */
    private OrderResponseDto getOrderResponseDto(OrderEntity savedOrder) {

        List<OrderItemResponseDto> orderItemList = savedOrder.getOrderItems()
                .stream()
                .map(itemDto -> OrderItemResponseDto.builder()
                        .orderPrice(itemDto.getOrderPrice())
                        .productName(itemDto.getProduct().getProductName())
                        .quantity(itemDto.getQuantity())
                        .build())
                .toList();

        return OrderResponseDto.builder()
                .orderId(savedOrder.getId())
                .storeType(savedOrder.getStore().getStoreType())
                .tableNumber(savedOrder.getTableNumber())
                .orderStatus(savedOrder.getStatus())
                .totalPrice(savedOrder.getTotalPrice())
                .createAt(savedOrder.getCreatedAt())
                .usedCardName(savedOrder.getUsedCardName())
                .orderItems(orderItemList)
                .build();
    }
}
