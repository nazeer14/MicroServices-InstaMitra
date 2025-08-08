package com.pack.service.impl;

import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import com.pack.common.dto.OrderRequestDTO;
import com.pack.entity.Order;
import com.pack.exception.ResourceNotFoundException;
import com.pack.repository.OrderRepository;
import com.pack.service.OrderService;
import com.pack.utils.OrderMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) {

        // Generate orderNumber
        //String orderNumber = "ORD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + generateRandomDigits(4);
        String orderNumber;
        do {
            orderNumber = "ORD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + generateRandomDigits(4);
        } while (orderRepository.existsByOrderNumber(orderNumber));
        Order order = OrderMapper.toEntity(requestDTO);
        order.setOrderNumber(orderNumber);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        return OrderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponseDTO getOrderByOrderNumber(String orderNumber){
        return orderRepository.findByOrderNumber(orderNumber)
                .map(OrderMapper::toDto)
                .orElseThrow(()->new EntityNotFoundException("Order not found"));
    }

    @Override
    public OrderResponseDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    @Override
    public List<OrderResponseDTO> getActiveOrders() {
        return orderRepository.findByStatus(OrderStatus.IN_PROGRESS)
                .stream().map(OrderMapper::toDto).toList();
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status)
                .stream().map(OrderMapper::toDto).toList();
    }

    @Override
    public List<OrderResponseDTO> getOrdersByProviderId(Long providerId) {
        return orderRepository.findByProviderId(providerId)
                .stream().map(OrderMapper::toDto).toList();
    }

    @Override
    public void updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findByOrderNumber(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public OrderResponseDTO updateOrder(Long orderId, OrderRequestDTO dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (dto.getAddress() != null) order.setAddress(dto.getAddress());
        if (dto.getScheduledDate() != null) order.setScheduledDate(dto.getScheduledDate());
        if (dto.getNotes() != null) order.setNotes(dto.getNotes());

        return OrderMapper.toDto(orderRepository.save(order));
    }



    @Override
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(OrderMapper::toDto).toList();
    }

    @Override
    public OrderResponseDTO getOrderByTransactionId(String transactionId) {
        return OrderMapper.toDto(orderRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found")));
    }

    @Override
    public OrderResponseDTO getOrderByRefundTransactionId(String refundTransactionId) {
        return OrderMapper.toDto(orderRepository.findByRefundTransactionId(refundTransactionId)
                .orElseThrow(() -> new EntityNotFoundException("Refund transaction not found")));
    }

    @Override
    public Page<OrderResponseDTO> getAllOrdersPageable(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderMapper::toDto);
    }

    @Override
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponseDTO> getOrdersByLocation(double lat, double lon, double radiusKm) {
        // Assume order entity has lat/lon fields
        return orderRepository.findWithinRadius(lat, lon, radiusKm)
                .stream().map(OrderMapper::toDto).toList();
    }

    @Override
    public List<OrderResponseDTO> getOrdersByStatus(Long id, String status) {
        return orderRepository.findByProviderIdAndStatus(id,status)
                .stream().map(OrderMapper::toDto).toList();

    }

    private String generateRandomDigits(int length) {
        return String.valueOf((int)(Math.random() * Math.pow(10, length)));
    }
}
