package com.pack.dto;

import com.pack.common.enums.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class OrderRequestDTO {
    @NotNull
    private Long userId;

    @NotNull
    private Long providerId;

    private String serviceName;
    private String serviceType;

    private LocalDateTime orderDate;

    private OrderStatus status;

    @NotNull
    private BigDecimal amount;

    private LocalDateTime scheduledDate;
    private LocalTime slotTimeStart;
    private LocalTime slotTimeEnd;
    private Long totalDuration;

    private String address;
    private Double latitude;
    private Double longitude;
    private String notes;


}
