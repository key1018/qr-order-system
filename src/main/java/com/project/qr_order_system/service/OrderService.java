package com.project.qr_order_system.service;

import com.project.qr_order_system.dto.order.OrderItemResponseDto;
import com.project.qr_order_system.dto.order.OrderRequestDto;
import com.project.qr_order_system.dto.order.OrderResponseDto;
import com.project.qr_order_system.model.*;
import com.project.qr_order_system.persistence.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * 주문 등록
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
            ProductEntity product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

            // 재고 감소
            product.removeStock(itemDto.getQuantity());

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
