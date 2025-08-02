package com.pack.common.dto;

import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import com.pack.common.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class OrderDTO {

    private Long id;

    // --- Order Metadata ---

    private String orderNumber; // e.g., ORD202507201234

    @PastOrPresent
    private LocalDateTime orderDate;

    @Future
    private LocalDateTime scheduledDate;

    private LocalTime slotTimeStart;

    private LocalTime slotTimeEnd;

    private Long totalDuration;

    private OrderStatus status;

    private LocalDateTime canceledAt;

    private Role canceledBy;

    private String reason;//if failed or canceled or in_progress

    @Size(max = 500)
    private String notes;

    // --- Payment Details ---

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal amount;

    private PaymentStatus paymentStatus;

    @Size(max = 100)
    private String transactionId;

    // --- Refund Details ---

    @Size(max = 100)
    private String refundTransactionId;

    @Size(max = 300)
    private String refundReason;

    private LocalDateTime refundDate;

    // --- Relationships ---


    private Long userId;


    private Long providerId;

    private String serviceName;

    private String serviceType;

    // --- Location ---

    private String address;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private Double latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private Double longitude;

    // --- Additional ---

    private boolean rated = false;

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    private Long otpForStart;

}
