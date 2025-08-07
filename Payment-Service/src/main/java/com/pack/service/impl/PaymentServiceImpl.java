package com.pack.service.impl;

import com.pack.client.OrderServiceClient;
import com.pack.client.OrderUpdateClient;
import com.pack.common.dto.OrderRequestDTO;
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
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    public String createPaymentOrder(BigDecimal amount,TransactionType transactionType, String bookingId) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100))); // Razorpay expects paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", bookingId);
        orderRequest.put("payment_capture", 1);

        OrderResponseDTO dto=new OrderResponseDTO();
        try{
          dto=orderServiceClient.getOrderDetailsByOrderId(bookingId);
        }catch (Exception e){
            log.error("Failed to get order-details: {}", e.getMessage());
        }
        if (dto == null || dto.getOrderNumber() == null) {
            throw new IllegalArgumentException("Invalid order Id. Please check.");
        }
        if(dto.getStatus().equals(OrderStatus.CANCELED)){
            return "Order is cancelled";
        }

        if(dto.getPaymentStatus().equals(PaymentStatus.PAID)){
            return "Amount already paid. please check.";
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
        Optional<Transaction> txOpt = transactionRepository.findByTransactionId(razorpayPaymentId);
        if(txOpt.isEmpty()){
            throw new IllegalArgumentException("Invalid Transaction Id not found");
        }
        Transaction tx = txOpt.get();
        tx.setPaymentStatus(PaymentStatus.PAID);
        tx.setRemarks("Payment successful via Razorpay");
        transactionRepository.save(tx);

        // Feign call to update order status
        try {
            orderUpdateClient.updateOrderStatus(tx.getId(),PaymentStatus.PAID.name());
        } catch (Exception e) {
            log.error("Failed to notify order-service: {}", e.getMessage());
        }
    }

    public String refundPayment(RefundRequestDTO dto) {
        try {
            // Check if already refunded
            Transaction txn = transactionRepository.findByTransactionId(dto.getTransactionId())
                    .orElseThrow(() -> new RuntimeException("Transaction not found"));

            if (PaymentStatus.REFUNDED.equals(txn.getPaymentStatus())) {
                return "Already refunded.";
            }

            if (dto.getAmount().compareTo(txn.getAmount()) > 0) {
                return "Refund amount exceeds transaction amount.";
            }

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

            return "Refund API failed with no ID returned.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Refund failed: " + e.getMessage();
        }
    }

    public OrderDetailsDTO fetchOrderFromOrderService(String paymentId) {
        try {
            return orderServiceClient.getOrderDetailsByPaymentId(paymentId);
        } catch (Exception e) {
            // Additional handling if fallback returns null
            log.warn("Failed to fetch order details: {}", e.getMessage());
            return null;
        }
    }

}

