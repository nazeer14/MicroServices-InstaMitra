package com.pack.service.impl;

import com.pack.common.dto.OrderCompletedDTO;
import com.pack.common.dto.OrderRequestDTO;
import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import com.pack.entity.Order;
import com.pack.repository.OrderRepository;
import com.pack.service.OrderService;
import com.pack.utils.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    // ✅ CREATE - after saving, cache the new order
    @Override
    @CachePut(value = "orders", key = "'id:' + #result.id")
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {
        String orderNumber;
        do {
            orderNumber = "ORD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    + generateRandomDigits(6);
        } while (orderRepository.existsByOrderNumber(orderNumber));

        Order order = OrderMapper.toEntity(requestDTO);
        order.setOrderNumber(orderNumber);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.UNPAID);

        return OrderMapper.toDto(orderRepository.save(order));
    }

    // ✅ READ - cache lookups
    @Override
    @Cacheable(value = "orders", key = "'number:' + #orderNumber")
    public OrderResponseDTO getOrderByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Override
    @Cacheable(value = "orders", key = "'id:' + #id")
    public OrderResponseDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }

    @Override
    public List<OrderResponseDTO> getActiveOrders() {
        return List.of();
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        return List.of();
    }

    @Override
    public List<OrderResponseDTO> getOrdersByProviderId(Long providerId) {
        return List.of();
    }

    // ✅ UPDATE - update cache after saving
    @Override
    @CachePut(value = "orders", key = "'id:' + #orderId")
    public OrderResponseDTO updateOrder(Long orderId, OrderRequestDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (dto.getAddress() != null) order.setAddress(dto.getAddress());
        if (dto.getScheduledDate() != null) order.setScheduledDate(dto.getScheduledDate());
        if (dto.getNotes() != null) order.setNotes(dto.getNotes());

        return OrderMapper.toDto(orderRepository.save(order));
    }

    // ✅ UPDATE status + evict related keys
    @Override
    @CacheEvict(value = "orders", key = "'number:' + #orderId")
    public void updateOrderStatus(String orderId, OrderCompletedDTO dto) {
        Order order = orderRepository.findByOrderNumber(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setStatus(dto.getOrderStatus());
        order.setPaymentStatus(dto.getPaymentStatus());
        order.setTransactionId(dto.getTransactionId());
        orderRepository.save(order);
    }

    // ✅ DELETE / CANCEL - evict cache
    @Override
    @CacheEvict(value = "orders", key = "'id:' + #orderId")
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    // Other queries (list, search, location-based) can be cached separately
    @Override
    @Cacheable(value = "orders", key = "'user:' + #userId")
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(OrderMapper::toDto).toList();
    }

    @Override
    @Cacheable(value = "orders", key = "'transaction:' + #transactionId")
    public OrderResponseDTO getOrderByTransactionId(String transactionId) {
        return OrderMapper.toDto(orderRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found")));
    }

    @Override
    @Cacheable(value = "orders", key = "'refund:' + #refundTransactionId")
    public OrderResponseDTO getOrderByRefundTransactionId(String refundTransactionId) {
        return OrderMapper.toDto(orderRepository.findByRefundTransactionId(refundTransactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refund transaction not found")));
    }

    @Override
    public Page<OrderResponseDTO> getAllOrdersPageable(Pageable pageable) {
        return null;
    }

    @Override
    public List<OrderResponseDTO> getOrdersByLocation(double lat, double lon, double radiusKm) {
        return List.of();
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(Long providerId, OrderStatus status) {
        return List.of();
    }

    private String generateRandomDigits(int length) {
        return String.format("%0" + length + "d", (int) (Math.random() * Math.pow(10, length)));
    }
}
