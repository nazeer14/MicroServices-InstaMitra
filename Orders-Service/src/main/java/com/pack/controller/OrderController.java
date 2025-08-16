package com.pack.controller;

import com.pack.common.dto.OrderCompletedDTO;
import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.enums.OrderStatus;
import com.pack.common.dto.OrderRequestDTO;
import com.pack.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody @Valid OrderRequestDTO requestDTO) {
        return ResponseEntity.ok(orderService.createOrder(requestDTO));
    }

    @GetMapping("/get-order/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable("id") Long orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }
    @GetMapping("/{orderId}/get")
    public ResponseEntity<OrderResponseDTO> getOrderByOrderNumber(@PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(orderService.getOrderByOrderNumber(orderId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<OrderResponseDTO>> getActiveOrders() {
        return ResponseEntity.ok(orderService.getActiveOrders());
    }
    /*
    *  Using Payment Service update Order status and Payment status, add transaction id
    * */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String orderId, @RequestBody OrderCompletedDTO dto) {
        orderService.updateOrderStatus(orderId, dto);
        Map<String,String> res=new HashMap<>();
        res.put("message","Order updated successfully");
        return ResponseEntity.ok(res);
    }
    @PutMapping("/{orderNumber}/update")
    public ResponseEntity<OrderResponseDTO> updateOrderDetails(
            @PathVariable Long orderNumber,
            @RequestBody @Valid OrderRequestDTO requestDTO) {
        return ResponseEntity.ok(orderService.updateOrder(orderNumber, requestDTO));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled successfully.");
    }


    @GetMapping("/status/by-provider/{providerId}")
    public ResponseEntity<List<OrderResponseDTO>> getActiveOrdersById(@PathVariable("providerId") Long providerId,@RequestParam("status") String status) {
        OrderStatus status1=OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(orderService.getOrdersByStatus(providerId, status1));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(OrderStatus.valueOf(status)));
    }

    @GetMapping("/by-provider/{providerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByProvider(@PathVariable("providerId") Long providerId) {
        return ResponseEntity.ok(orderService.getOrdersByProviderId(providerId));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<OrderResponseDTO> getOrderByTransactionId(@PathVariable String transactionId) {
        return ResponseEntity.ok(orderService.getOrderByTransactionId(transactionId));
    }

    @GetMapping("/refund/{refundTransactionId}")
    public ResponseEntity<OrderResponseDTO> getOrderByRefundTransactionId(@PathVariable String refundTransactionId) {
        return ResponseEntity.ok(orderService.getOrderByRefundTransactionId(refundTransactionId));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<OrderResponseDTO>> getAllOrdersPageable(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrdersPageable(pageable));
    }

    @GetMapping("/location")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersByLocation(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "5.0") double radiusKm) {
        return ResponseEntity.ok(orderService.getOrdersByLocation(lat, lon, radiusKm));
    }
}
