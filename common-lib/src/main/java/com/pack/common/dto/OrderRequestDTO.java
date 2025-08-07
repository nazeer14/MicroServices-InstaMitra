package com.pack.common.dto;

import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class OrderRequestDTO {

    private Long userId;

    private Long providerId;

    private String serviceName;

    private String serviceType;

    private LocalDateTime orderDate;

    private OrderStatus status;

    private BigDecimal amount;

    private LocalDateTime scheduledDate;
    private LocalTime slotTimeStart;
    private LocalTime slotTimeEnd;
    private Long totalDuration;

    private String address;
    private Double latitude;
    private Double longitude;
    private String notes;

    private PaymentStatus paymentStatus;


}
