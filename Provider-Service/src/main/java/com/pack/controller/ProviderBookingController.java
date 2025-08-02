package com.pack.controller;

import com.pack.common.dto.OrderDTO;
import com.pack.common.dto.OrderResponseDTO;
import com.pack.service.ProviderOrdersService;
import com.pack.service.ProviderService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/provider/v1/booking")
@EnableMethodSecurity
@EnableWebSecurity
@AllArgsConstructor
public class ProviderBookingController {

    private final ProviderService providerService;

    private final ProviderOrdersService ordersService;


    @GetMapping("/{providerId}/get-orders")
    public ResponseEntity<List<OrderResponseDTO>> getAllBookingsByProviderId(@PathVariable("providerId") Long id) {
        providerService.getById(id);
        List<OrderResponseDTO> orders=ordersService.getOrdersByProviderId(id);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/getOrderById/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderByOrderId(@PathVariable("id") Long id, @RequestParam("orderId") Long orderId){
        return ResponseEntity.ok(ordersService.getOrderByOrderId(id,orderId));
    }



}
