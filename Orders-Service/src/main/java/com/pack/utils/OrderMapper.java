package com.pack.utils;

import com.pack.common.dto.OrderResponseDTO;
import com.pack.common.dto.OrderRequestDTO;
import com.pack.entity.Order;

public class OrderMapper {

    public static Order toEntity(OrderRequestDTO dto) {
        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setProviderId(dto.getProviderId());
        order.setAmount(dto.getAmount());
        order.setServiceName(dto.getServiceName());
        order.setServiceType(dto.getServiceType());
        order.setScheduledDate(dto.getScheduledDate());
        order.setSlotTimeStart(dto.getSlotTimeStart());
        order.setSlotTimeEnd(dto.getSlotTimeEnd());
        order.setTotalDuration(dto.getTotalDuration());
        order.setLatitude(dto.getLatitude());
        order.setLongitude(dto.getLongitude());
        order.setAddress(dto.getAddress());
        order.setNotes(dto.getNotes());
        return order;
    }

    public static OrderResponseDTO toDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setOrderDate(order.getOrderDate());
        dto.setAmount(order.getAmount());
        dto.setServiceName(order.getServiceName());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setTransactionId(order.getTransactionId());
        dto.setUserId(order.getUserId());
        dto.setProviderId(order.getProviderId());
        dto.setAddress(order.getAddress());
        dto.setNotes(order.getNotes());
        dto.setLatitude(order.getLatitude());
        dto.setLongitude(order.getLongitude());
        dto.setScheduledDate(order.getScheduledDate());
        dto.setSlotTimeStart(order.getSlotTimeStart());
        dto.setSlotTimeEnd(order.getSlotTimeEnd());
        dto.setTotalDuration(order.getTotalDuration());

        return dto;
    }

}
