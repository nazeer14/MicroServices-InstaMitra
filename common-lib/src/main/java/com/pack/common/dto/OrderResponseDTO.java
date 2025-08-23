package com.pack.common.dto;

import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@NoArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String serviceName;
    private String transactionId;
    private Long userId;
    private Long providerId;
    private LocalDateTime scheduledDate;
    private LocalTime slotTimeStart;
    private LocalTime slotTimeEnd;
    private Long totalDuration;
    private String address;
    private Double latitude;
    private Double longitude;
    private String notes;

}

