package com.pack.client.fallback;

import com.pack.client.OrderServiceClient;
import com.pack.common.dto.OrderResponseDTO;
import com.pack.dto.OrderDetailsDTO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderServiceFallbackFactory implements FallbackFactory<OrderServiceClient> {
    @Override
    public OrderServiceClient create(Throwable cause) {
        return new OrderServiceClient() {
            @Override
            public OrderDetailsDTO getOrderDetailsByPaymentId(String paymentId) {
                log.error("Fallback triggered for OrderServiceClient.getOrderDetailsByPaymentId. Reason: {}", cause.getMessage());
                return null;
            }

            @Override
            public OrderResponseDTO getOrderDetailsByOrderId(String orderId) {
                log.error("Fallback triggered for OrderServiceClient.getOrderDetailsByOrderId. Reason: {}", cause.getMessage());
                return null;
            }
        };
    }
}
