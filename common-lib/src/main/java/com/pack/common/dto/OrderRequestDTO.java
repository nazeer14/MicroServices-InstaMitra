package com.pack.common.dto;

import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class OrderRequestDTO {

    @NotNull(message = "userId is mandatory")
    private Long userId;

    @NotNull(message = "providerId is mandatory")
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
