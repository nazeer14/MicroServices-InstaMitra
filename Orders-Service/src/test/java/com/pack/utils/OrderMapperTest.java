package com.pack.utils;

import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import com.pack.dto.OrderRequestDTO;
import com.pack.entity.Order;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    @Test
    void testToEntityAndToDtoMappingConsistency() {

        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(101L);
        request.setProviderId(202L);
        request.setAmount(new BigDecimal("499.99"));
        request.setServiceName("AC Cleaning");
        request.setServiceType("Home");
        request.setScheduledDate(LocalDateTime.of(2025, 8, 5, 14, 0));
        request.setSlotTimeStart(LocalTime.of(14, 0));
        request.setSlotTimeEnd(LocalTime.of(16, 0));
        request.setTotalDuration(120L);
        request.setAddress("123 Tech Street");
        request.setLatitude(12.9716);
        request.setLongitude(77.5946);
        request.setNotes("Please bring your own tools.");

        // Step 2: Map to Entity
        Order entity = OrderMapper.toEntity(request);
        entity.setId(1L);
        entity.setOrderNumber("ORD123");
        entity.setStatus(OrderStatus.PLACED);
        entity.setOrderDate(LocalDateTime.now());
        entity.setPaymentStatus(PaymentStatus.PENDING);
        entity.setTransactionId("TXN456"+System.currentTimeMillis());

        // Step 3: Map to Response DTO
        OrderResponseDTO response = OrderMapper.toDto(entity);

        // Step 4: Validate consistency
        assertEquals(request.getUserId(), response.getUserId());
        assertEquals(request.getProviderId(), response.getProviderId());
        assertEquals(request.getAmount(), response.getAmount());
        assertEquals(request.getAddress(), response.getAddress());
        assertEquals(request.getNotes(), response.getNotes());

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getOrderNumber(), response.getOrderNumber());
        assertEquals(entity.getStatus(), response.getStatus());
        assertEquals(entity.getPaymentStatus(), response.getPaymentStatus());
        assertEquals(entity.getTransactionId(), response.getTransactionId());
    }
}
