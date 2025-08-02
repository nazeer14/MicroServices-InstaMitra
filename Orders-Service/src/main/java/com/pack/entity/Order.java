package com.pack.entity;

import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_order_number", columnList = "orderNumber"),
                @Index(name = "idx_user_id", columnList = "userId"),
                @Index(name = "idx_provider_id", columnList = "providerId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Order Metadata ---

    @NotBlank
    @Column(nullable = false, unique = true,updatable = false)
    private String orderNumber; // e.g., ORD202507201234

    @NotNull
    @Column(nullable = false)
    private LocalDateTime orderDate;

    private LocalDateTime scheduledDate;

    private LocalTime slotTimeStart;

    private LocalTime slotTimeEnd;

    private Long totalDuration;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "who_canceled")
    private String whoCanceled;

    @Column(name = "reason")
    private String reason;//if failed or canceled or in_progress

    @Size(max = 500)
    private String notes;

    // --- Payment Details ---

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Size(max = 100)
    @Column(unique = true)
    private String transactionId;

    // --- Refund Details ---

    @Size(max = 100)
    @Column(unique = true)
    private String refundTransactionId;

    @Size(max = 300)
    private String refundReason;

    private LocalDateTime refundDate;

    // --- Relationships ---

    @NotNull
    @Column(nullable = false)
    private Long userId;

    @NotNull
    @Column(nullable = false)
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

    private boolean isRated = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Long otpForStart;

    @Version
    private int version;
}
