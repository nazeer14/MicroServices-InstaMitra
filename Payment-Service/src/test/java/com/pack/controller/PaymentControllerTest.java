package com.pack.controller;

import com.pack.dto.OrderDetailsDTO;
import com.pack.dto.RefundRequestDTO;
import com.pack.enums.TransactionType;
import com.pack.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    private final TransactionType transactionType=TransactionType.DEBIT;
    // Test: createPayment (Success)
    @Test
    void testCreatePayment_Success() throws Exception {
        BigDecimal amount = new BigDecimal("100.00");
        String bookingId = "BOOK123";
        String mockOrder = "order_abc123";



        when(paymentService.createPaymentOrder(amount,transactionType, bookingId)).thenReturn(mockOrder);

        ResponseEntity<?> response = paymentController.createPayment(amount,transactionType.name(), bookingId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(mockOrder, response.getBody());
        verify(paymentService).createPaymentOrder(amount,transactionType, bookingId);
    }

    // Test: createPayment (Exception)
    @Test
    void testCreatePayment_Exception() throws Exception {
        BigDecimal amount = new BigDecimal("100.00");
        String bookingId = "BOOK123";

        when(paymentService.createPaymentOrder(amount,transactionType, bookingId))
                .thenThrow(new RuntimeException("Some error"));

        ResponseEntity<?> response = paymentController.createPayment(amount,transactionType.name(), bookingId);

        assertEquals(500, response.getStatusCode().value());

        assertTrue(Objects.toString(response.getBody(), "").contains("Error: Some error"));
        verify(paymentService).createPaymentOrder(amount,transactionType, bookingId);
    }

    // Test: handleSuccess
    @Test
    void testHandleSuccess() {
        Map<String, String> payload = new HashMap<>();
        payload.put("razorpay_payment_id", "pay_123");
        payload.put("razorpay_order_id", "order_456");
        payload.put("razorpay_signature", "signature_789");

        ResponseEntity<?> response = paymentController.handleSuccess(payload);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Payment verified and recorded", response.getBody());

        verify(paymentService).handlePaymentSuccess("pay_123", "order_456", "signature_789");
    }

    // Test: refund
    @Test
    void testRefund() {
        RefundRequestDTO dto = new RefundRequestDTO();
        dto.setTransactionId("2mfkrkfJjdl");
        dto.setAmount(new BigDecimal("100.00"));
        dto.setReason("Duplicate");

        when(paymentService.refundPayment(dto)).thenReturn("Refund successful");

        ResponseEntity<String> response = paymentController.refund(dto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Refund successful", response.getBody());
        verify(paymentService).refundPayment(dto);
    }

    // Test: getOrderDetails
    @Test
    void testGetOrderDetails() {
        String paymentId = "pay_002";
        OrderDetailsDTO dto = new OrderDetailsDTO();
        dto.setPaymentId(paymentId);
        dto.setAmount(new BigDecimal("150.00"));

        when(paymentService.fetchOrderFromOrderService(paymentId)).thenReturn(dto);

        ResponseEntity<OrderDetailsDTO> response = paymentController.getOrderDetails(paymentId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(dto, response.getBody());
        verify(paymentService).fetchOrderFromOrderService(paymentId);
    }
}
