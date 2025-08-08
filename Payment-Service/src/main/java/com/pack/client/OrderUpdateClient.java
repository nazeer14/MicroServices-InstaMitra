package com.pack.client;

import com.pack.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "orders-service", url = "http://localhost:8080/orders-service/", configuration = FeignConfig.class)
public interface OrderUpdateClient {

    @PatchMapping("/orders/v1/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") String orderId, @RequestParam String status);
}

