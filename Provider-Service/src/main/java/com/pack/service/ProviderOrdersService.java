package com.pack.service;

import com.pack.common.dto.OrderDTO;
import com.pack.common.dto.OrderResponseDTO;

import java.util.List;

public interface ProviderOrdersService {


    void getOrderRequest();

    void acceptOrder(Long id);

    void rejectOrder(Long bookingId);

    List<OrderResponseDTO> getOrdersByProviderId(Long id);

    OrderResponseDTO getOrderByOrderId(Long id, Long orderId);
}
