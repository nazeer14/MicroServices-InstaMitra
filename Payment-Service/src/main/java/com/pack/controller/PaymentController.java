package com.pack.controller;

import com.pack.dto.OrderDetailsDTO;
import com.pack.dto.RefundRequestDTO;
import com.pack.enums.TransactionType;
import com.pack.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestParam BigDecimal amount,
                                           @RequestParam String type,
                                           @RequestParam String bookingId) {
        try {
            TransactionType transactionType=TransactionType.valueOf(type.toUpperCase());
            String order = paymentService.createPaymentOrder(amount,transactionType , bookingId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @PostMapping("/success")
    public ResponseEntity<?> handleSuccess(@RequestBody Map<String, String> payload) {
        String razorpayPaymentId = payload.get("razorpay_payment_id");
        String orderId = payload.get("razorpay_order_id");
        String signature = payload.get("razorpay_signature");

        paymentService.handlePaymentSuccess(razorpayPaymentId, orderId, signature);
        return ResponseEntity.ok("Payment verified and recorded");
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refund(@RequestBody RefundRequestDTO dto) {
        String result = paymentService.refundPayment(dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/order-details/{paymentId}")
    public ResponseEntity<OrderDetailsDTO> getOrderDetails(@PathVariable String paymentId) {
        return ResponseEntity.ok(paymentService.fetchOrderFromOrderService(paymentId));
    }
}
