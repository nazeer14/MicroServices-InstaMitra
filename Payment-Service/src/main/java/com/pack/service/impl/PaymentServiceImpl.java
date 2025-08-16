package com.pack.service.impl;

import com.pack.client.OrderServiceClient;
import com.pack.client.OrderUpdateClient;
import com.pack.common.dto.OrderCompletedDTO;
import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.enums.OrderStatus;
import com.pack.common.enums.PaymentStatus;
import com.pack.dto.OrderDetailsDTO;
import com.pack.dto.RefundRequestDTO;
import com.pack.entity.Transaction;
import com.pack.enums.TransactionType;
import com.pack.repository.TransactionRepository;
import com.pack.service.PaymentService;
import com.razorpay.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private final TransactionRepository transactionRepository;
    private final OrderServiceClient orderServiceClient;
    private final OrderUpdateClient orderUpdateClient;

    @Override
    public String createPaymentOrder(BigDecimal amount, TransactionType transactionType, String bookingId) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100))); // Razorpay expects paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", bookingId);
        orderRequest.put("payment_capture", 1);

        OrderResponseDTO dto;
        try {
            dto = orderServiceClient.getOrderDetailsByOrderId(bookingId);
        } catch (Exception e) {
            log.error("Failed to get order-details: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to fetch order details");
        }

        if (dto == null || dto.getOrderNumber() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid order Id. Please check.");
        }
        if (dto.getStatus().equals(OrderStatus.CANCELED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is cancelled");
        }
        if (dto.getPaymentStatus().equals(PaymentStatus.PAID)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount already paid. Please check.");
        }

        Order order = razorpay.orders.create(orderRequest);

        // Save initial transaction
        Transaction tx = Transaction.builder()
                .orderId(bookingId)
                .transactionId(order.get("id"))
                .amount(amount)
                .paymentStatus(PaymentStatus.PENDING)
                .transactionDate(LocalDateTime.now())
                .transactionType(transactionType)
                .isRefunded(false)
                .paymentMethod("Online")
                .build();
        transactionRepository.save(tx);

        return order.toJson().toString(); // Return order JSON to frontend
    }

    @Override
    public void handlePaymentSuccess(String razorpayPaymentId, String orderId, String signature) {
        Transaction tx = transactionRepository.findByTransactionId(razorpayPaymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid Transaction Id"));

        tx.setPaymentStatus(PaymentStatus.PAID);
        tx.setRemarks("Payment successful via Razorpay");
        transactionRepository.save(tx);

        // Feign call to update order status
        try {
            OrderCompletedDTO dto = new OrderCompletedDTO();
            dto.setOrderStatus(OrderStatus.COMPLETED);
            dto.setPaymentStatus(PaymentStatus.PAID);
            dto.setTransactionId(tx.getTransactionId());
            orderUpdateClient.updateOrderStatus(tx.getOrderId(), dto);
        } catch (Exception e) {
            log.error("Failed to notify order-service: {}", e.getMessage());
        }
    }

    @Override
    public String refundPayment(RefundRequestDTO dto) {
        Transaction txn = transactionRepository.findByTransactionId(dto.getTransactionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        if (PaymentStatus.REFUNDED.equals(txn.getPaymentStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already refunded");
        }
        if (dto.getAmount().compareTo(txn.getAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refund amount exceeds transaction amount");
        }

        try {
            // Call Razorpay Refund API
            String credentials = keyId + ":" + keySecret;
            String base64Creds = Base64.getEncoder().encodeToString(credentials.getBytes());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Basic " + base64Creds);

            Map<String, Object> body = new HashMap<>();
            body.put("amount", dto.getAmount()); // optional for partial
            body.put("notes", Map.of("reason", dto.getReason()));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.razorpay.com/v1/payments/" + dto.getTransactionId() + "/refund",
                    request,
                    Map.class
            );

            Map<String, Object> resBody = response.getBody();
            if (resBody != null && resBody.get("id") != null) {
                txn.setRefundTransactionId(resBody.get("id").toString());
                txn.setPaymentStatus(PaymentStatus.REFUNDED);
                txn.setRemarks(dto.getReason());
                txn.setRefundDate(LocalDateTime.now());
                transactionRepository.save(txn);
                return "Refund successful. Refund ID: " + txn.getRefundTransactionId();
            }

            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Refund API failed with no ID returned");
        } catch (ResponseStatusException ex) {
            throw ex; // rethrow known HTTP exceptions
        } catch (Exception e) {
            log.error("Refund failed: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Refund failed: " + e.getMessage());
        }
    }

    @Override
    public OrderDetailsDTO fetchOrderFromOrderService(String paymentId) {
        try {
            return orderServiceClient.getOrderDetailsByPaymentId(paymentId);
        } catch (Exception e) {
            log.warn("Failed to fetch order details: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to fetch order details");
        }
    }
}
