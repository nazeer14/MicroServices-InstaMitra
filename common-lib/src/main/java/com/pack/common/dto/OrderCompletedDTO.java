package com.pack.common.dto;

import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import lombok.Data;

@Data
public class OrderCompletedDTO {
    private OrderStatus orderStatus;
    private PaymentStatus paymentStatus;
    private String transactionId;
}
