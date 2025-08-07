package com.pack.dto;

import com.pack.common.enums.PaymentStatus;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {

    private Long userId;
    private Long workerId;
    private String bookingId;
    private BigDecimal amount;
    private String paymentMethod;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private String remarks;
}
