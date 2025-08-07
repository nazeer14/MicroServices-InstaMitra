package com.pack.dto;

import com.pack.common.enums.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {
    private String bookingId;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private String transactionId;
    private LocalDateTime transactionDate;
    private Boolean isRefunded;
    private String refundTransactionId;
    private LocalDateTime refundDate;
    private String remarks;
}
