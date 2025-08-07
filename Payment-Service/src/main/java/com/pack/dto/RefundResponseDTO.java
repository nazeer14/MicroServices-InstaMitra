package com.pack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RefundResponseDTO {
    private String refundTxnId;
    private String status;
    private String message;
}
