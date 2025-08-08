package com.pack.service;


import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.enums.OrderStatus;
import com.pack.common.dto.OrderRequestDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO requestDTO);

    OrderResponseDTO getOrderByOrderNumber(String orderNumber);

    OrderResponseDTO getOrderById(Long id);
    List<OrderResponseDTO> getActiveOrders();
    List<OrderResponseDTO> getOrdersByStatus(OrderStatus status);
    List<OrderResponseDTO> getOrdersByProviderId(Long providerId);
    List<OrderResponseDTO> getOrdersByUserId(Long userId);
    OrderResponseDTO getOrderByTransactionId(String transactionId);
    OrderResponseDTO getOrderByRefundTransactionId(String refundTransactionId);
    Page<OrderResponseDTO> getAllOrdersPageable(Pageable pageable);
    List<OrderResponseDTO> getOrdersByLocation(double lat, double lon, double radiusKm);


    List<OrderResponseDTO> getOrdersByStatus(Long id, String status);

    void updateOrderStatus(String orderId, OrderStatus orderStatus);

    OrderResponseDTO updateOrder(Long orderId, @Valid OrderRequestDTO requestDTO);

    void cancelOrder(Long orderId);
}

