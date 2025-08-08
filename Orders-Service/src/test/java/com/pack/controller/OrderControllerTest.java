package com.pack.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.pack.auth.JwtAuthFilter;
import com.pack.common.dto.OrderRequestDTO;
import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.enums.OrderStatus;
import com.pack.service.OrderService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private OrderResponseDTO sampleOrder() {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(1L);
        dto.setUserId(123L); // for tests expecting this
        dto.setOrderNumber("ORD123"); // change to ORD234 in specific tests if needed
        dto.setStatus(OrderStatus.IN_PROGRESS);
        return dto;
    }


    // =========================
    // ✅ HAPPY PATH TESTS
    // =========================


    @Test
    @DisplayName("POST /orders/v1/create - should create an order")
    void testCreateOrder() throws Exception {
        OrderRequestDTO req = new OrderRequestDTO();
        req.setUserId(1L); // ✅ Required fields
        req.setProviderId(2L);
        req.setAmount(BigDecimal.valueOf(500));
        req.setOrderDate(LocalDateTime.now());

        OrderResponseDTO resp = sampleOrder(); // Assume this returns a valid OrderResponseDTO

        Mockito.when(orderService.createOrder(any(OrderRequestDTO.class)))
                .thenReturn(resp);

        mockMvc.perform(post("/orders/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)).andExpect(jsonPath("$.orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/get-order/{id} - should return order by ID")
    void testGetOrderById() throws Exception {
        Mockito.when(orderService.getOrderById(1L)).thenReturn(sampleOrder());

        mockMvc.perform(get("/orders/v1/get-order/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/{orderId}/get - should return order by order number")
    void testGetOrderByOrderNumber() throws Exception {
        Mockito.when(orderService.getOrderByOrderNumber("ORD123")).thenReturn(sampleOrder());

        mockMvc.perform(get("/orders/v1/{orderId}/get", "ORD123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/active - should return active orders")
    void testGetActiveOrders() throws Exception {
        Mockito.when(orderService.getActiveOrders()).thenReturn(List.of(sampleOrder()));

        mockMvc.perform(get("/orders/v1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("PATCH /orders/v1/{orderId}/status - should update order status")
    void testUpdateOrderStatus() throws Exception {
        mockMvc.perform(patch("/orders/v1/{orderId}/status", 1L)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order status updated successfully"));
    }

    @Test
    @DisplayName("PUT /orders/v1/{orderId}/update - should update order details")
    void testUpdateOrderDetails() throws Exception {
        OrderRequestDTO request = new OrderRequestDTO();
        request.setUserId(123L);
        request.setProviderId(1L);
        request.setAmount(BigDecimal.valueOf(500));
        request.setOrderDate(LocalDateTime.now());

        OrderResponseDTO resp = sampleOrder();
        resp.setUserId(123L);
        resp.setOrderNumber("ORD234");

        Mockito.when(orderService.updateOrder(eq(1L), any(OrderRequestDTO.class)))
                .thenReturn(resp);

        mockMvc.perform(put("/orders/v1/{orderId}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(123L))
                .andExpect(jsonPath("$.orderNumber").value("ORD234"));
    }


    @Test
    @DisplayName("POST /orders/v1/{orderId}/cancel - should cancel an order")
    void testCancelOrder() throws Exception {
        mockMvc.perform(post("/orders/v1/{orderId}/cancel", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Order cancelled successfully."));
    }

    @Test
    @DisplayName("GET /orders/v1/status/by-provider/{providerId} - should return 400 for invalid enum value")
    void testGetOrders_InvalidEnum() throws Exception {
        mockMvc.perform(get("/orders/v1/status/by-provider/{providerId}", 1L)
                        .param("status", "NOT_A_REAL_STATUS"))
                .andExpect(jsonPath("$.message")
                        .value("No enum constant com.pack.common.enums.OrderStatus.NOT_A_REAL_STATUS"));
    }


    @Test
    @DisplayName("GET /orders/v1/status/{status} - should return orders by status")
    void testGetOrdersByStatus() throws Exception {
        Mockito.when(orderService.getOrdersByStatus(OrderStatus.ACTIVE))
                .thenReturn(List.of(sampleOrder()));

        mockMvc.perform(get("/orders/v1/status/{status}", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/provider/{providerId} - should return orders by provider")
    void testGetOrdersByProvider() throws Exception {
        Mockito.when(orderService.getOrdersByProviderId(1L)).thenReturn(List.of(sampleOrder()));

        mockMvc.perform(get("/orders/v1/provider/{providerId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/user/{userId} - should return orders by user")
    void testGetOrdersByUser() throws Exception {
        Mockito.when(orderService.getOrdersByUserId(1L)).thenReturn(List.of(sampleOrder()));

        mockMvc.perform(get("/orders/v1/user/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/transaction/{transactionId} - should return order by transaction ID")
    void testGetOrderByTransactionId() throws Exception {
        Mockito.when(orderService.getOrderByTransactionId("TX123")).thenReturn(sampleOrder());

        mockMvc.perform(get("/orders/v1/transaction/{transactionId}", "TX123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/refund/{refundTransactionId} - should return order by refund transaction ID")
    void testGetOrderByRefundTransactionId() throws Exception {
        Mockito.when(orderService.getOrderByRefundTransactionId("RF123")).thenReturn(sampleOrder());

        mockMvc.perform(get("/orders/v1/refund/{refundTransactionId}", "RF123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/page - should return paginated orders")
    void testGetAllOrdersPageable() throws Exception {
        Mockito.when(orderService.getAllOrdersPageable(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleOrder()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/orders/v1/page?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD123"));
    }

    @Test
    @DisplayName("GET /orders/v1/location - should return orders by location")
    void testGetOrdersByLocation() throws Exception {
        Mockito.when(orderService.getOrdersByLocation(10.0, 20.0, 5.0))
                .thenReturn(List.of(sampleOrder()));

        mockMvc.perform(get("/orders/v1/location")
                        .param("lat", "10.0")
                        .param("lon", "20.0")
                        .param("radiusKm", "5.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderNumber").value("ORD123"));
    }

    @Test
    void createOrder_Success() throws Exception {
        Mockito.when(orderService.createOrder(any())).thenReturn(sampleOrder());

        OrderRequestDTO req = new OrderRequestDTO();
        req.setUserId(123L);
        req.setProviderId(1L);
        req.setAmount(BigDecimal.valueOf(500));
        req.setOrderDate(LocalDateTime.now());

        mockMvc.perform(post("/orders/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD123"));


    }

    @Test
    void getOrderById_Success() throws Exception {
        Mockito.when(orderService.getOrderById(1L)).thenReturn(sampleOrder());

        mockMvc.perform(get("/orders/v1/get-order/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD123"));
    }

    @Test
    void getAllOrdersPageable_Success() throws Exception {
        Mockito.when(orderService.getAllOrdersPageable(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(sampleOrder()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/orders/v1/page?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderNumber").value("ORD123"));
    }

    // =========================
    // ❌ ERROR CASE TESTS
    // =========================

    @Test
    @DisplayName("POST /orders/v1/create - Validation failure with empty body")
    void createOrder_ValidationFailure() throws Exception {
        mockMvc.perform(post("/orders/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.providerId").value("providerId is mandatory"))
                .andExpect(jsonPath("$.userId").value("userId is mandatory"));
    }


    @Test
    @DisplayName("PATCH /{orderId}/status - Invalid enum value should throw 400")
    void updateOrderStatus_InvalidEnum() throws Exception {
        mockMvc.perform(patch("/orders/v1/{orderId}/status", 1L)
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().is4xxClientError()); // Could be 400 or 500 based on exception handler
    }

    @Test
    @DisplayName("GET /get-order/{id} - Order not found should return 404")
    void getOrderById_NotFound() throws Exception {
        Mockito.when(orderService.getOrderById(99L))
                .thenThrow(new EmptyResultDataAccessException(1));

        mockMvc.perform(get("/orders/v1/get-order/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /{orderId}/update - Invalid body should return 400")
    void updateOrderDetails_InvalidBody() throws Exception {
        // Invalid JSON string
        mockMvc.perform(put("/orders/v1/{orderId}/update", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("INVALID_JSON"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /location - Missing params should return 400")
    void getOrdersByLocation_MissingParams() throws Exception {
        mockMvc.perform(get("/orders/v1/location")
                        .param("lat", "10.0")) // Missing lon and radiusKm
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /{orderId}/cancel - Service throws exception")
    void cancelOrder_Exception() throws Exception {
        doThrow(new RuntimeException("Something went wrong"))
                .when(orderService).cancelOrder(1L);

        mockMvc.perform(post("/orders/v1/{orderId}/cancel", 1L))
                .andExpect(status().is5xxServerError());
    }

}

