package com.pack.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailsDTO {
    private String orderId;
    private String paymentId;
    private BigDecimal amount;
    // Add more fields as needed
}
