package com.pack.service;

import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.enums.OrderStatus;
import com.pack.exception.ProviderNotFoundException;
import com.pack.repository.ProviderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderOrderServiceImpl implements ProviderOrdersService {

    private static final Logger log = LoggerFactory.getLogger(ProviderOrderServiceImpl.class);

    private final RestTemplate restTemplate;

    private final ProviderRepository providerRepository;

    @Value("${external.orders-service.base-url}")
    private String ordersServiceBaseUrl;

    @Override
    public void getOrderRequest() {
    }

    @Override
    public void acceptOrder(Long id) {

    }

    @Override
    public void rejectOrder(Long bookingId) {

    }

    @Override
    @Retry(name = "providerServiceRetry")
    @CircuitBreaker(name = "providerServiceCB", fallbackMethod = "fallbackOrders")
    public List<OrderResponseDTO> getOrdersByProviderId(Long providerId) {
        ResponseEntity<List<OrderResponseDTO>> response = restTemplate.exchange(
                ordersServiceBaseUrl +"/orders/v1/provider/"+providerId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<OrderResponseDTO>>() {}
        );
        return response.getBody();
    }

    public List<OrderResponseDTO> fallbackOrders(Long providerId, Throwable ex) {
        log.error("Fallback triggered for getOrdersByProviderId. Provider ID: {}. Reason: {}", providerId, ex.getMessage(), ex);

        if (ex instanceof HttpServerErrorException) {
            log.warn("External service returned 5xx error.");
        } else if (ex instanceof HttpClientErrorException) {
            log.warn("External service returned 4xx error.");
        } else if (ex instanceof ResourceAccessException) {
            log.warn("External service not reachable or timed out.");
        } else {
            log.warn("Unknown error while calling external service.");
        }

        return Collections.emptyList();
    }

    @Override
    @Retry(name = "ordersServiceRetry")
    @CircuitBreaker(name = "ordersServiceCB", fallbackMethod = "fallbackSingleOrder")
    public OrderResponseDTO getOrderByOrderId(Long providerId, Long orderId) {
        // Ensure provider exists
        providerRepository.findById(providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));

        String url = String.format("%s/orders/v1/%d/get-order", ordersServiceBaseUrl, orderId);

        try {
            ResponseEntity<OrderResponseDTO> orderDTO = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<OrderResponseDTO>() {}
            );
            return orderDTO.getBody();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
    }


    public OrderResponseDTO fallbackSingleOrder(Long providerId, Long orderId, Throwable throwable) {
        log.error("Fallback triggered for providerId={}, orderId={}", providerId, orderId, throwable);
        return OrderResponseDTO.builder()
                .id(orderId)
                .status(OrderStatus.FAILED)
                .notes("Order service unavailable. Please try later.")
                .build();
    }

}
