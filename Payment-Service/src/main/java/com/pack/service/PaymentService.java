package com.pack.service;

import com.pack.dto.OrderDetailsDTO;
import com.pack.dto.RefundRequestDTO;
import com.pack.enums.TransactionType;
import com.razorpay.RazorpayException;

import java.math.BigDecimal;

public interface PaymentService {

    String createPaymentOrder(BigDecimal amount, TransactionType transactionType, String bookingId) throws RazorpayException;

    void handlePaymentSuccess(String razorpayPaymentId, String orderId, String signature);

    String refundPayment(RefundRequestDTO dto);

    OrderDetailsDTO fetchOrderFromOrderService(String paymentId);
}
