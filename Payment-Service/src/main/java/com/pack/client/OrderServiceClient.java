package com.pack.client;

import com.pack.client.fallback.OrderServiceFallbackFactory;
import com.pack.common.dto.OrderResponseDTO;
import com.pack.config.FeignConfig;
import com.pack.dto.OrderDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "orders-service",
        url = "http://localhost:8080/orders-service/",
        configuration = FeignConfig.class,
        fallbackFactory = OrderServiceFallbackFactory.class
)
public interface OrderServiceClient {
    @GetMapping("/api/v1/orders/transaction/{paymentId}")
    OrderDetailsDTO getOrderDetailsByPaymentId(@PathVariable String paymentId);

    @GetMapping("/api/v1/orders/{orderId}/get")
    OrderResponseDTO getOrderDetailsByOrderId(@PathVariable String orderId);
}


