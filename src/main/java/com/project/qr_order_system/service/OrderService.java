package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.order.OrderItemResponseDto;
import com.project.qr_order_system.dto.order.OrderRequestDto;
import com.project.qr_order_system.dto.order.OrderResponseDto;
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
        notificationService.sendOrderAlert(store.getId(), responseDto);

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

        return getOrderResponseDto(order);
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
