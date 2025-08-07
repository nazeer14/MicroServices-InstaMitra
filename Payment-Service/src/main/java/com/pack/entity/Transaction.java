package com.pack.entity;

import com.pack.common.enums.PaymentStatus;
import com.pack.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false,updatable = false)
    private String orderId;

    @Column(precision = 10, scale = 2, nullable = false, updatable = false)
    private BigDecimal amount;

    @Column(precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String paymentMethod; // e.g., UPI, CARD, WALLET

    @Column(unique = true,nullable = false,updatable = false)
    private String transactionId; // From payment gateway

    @Column(updatable = false)
    private LocalDateTime transactionDate;

    private Boolean isRefunded;

    private String refundTransactionId;

    private LocalDateTime refundDate;


    private String remarks;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;//CREDIT , DEBIT , REFUND

    @PrePersist
    public void prePersist() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }

}
