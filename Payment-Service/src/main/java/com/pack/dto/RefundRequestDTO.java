package com.pack.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class RefundRequestDTO {
    private String transactionId;
    private BigDecimal amount;
    private String reason;
}

