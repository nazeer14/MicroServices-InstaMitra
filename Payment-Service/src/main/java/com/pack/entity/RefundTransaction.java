package com.pack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "refund_transactions")
public class RefundTransaction {
    @Id
    private String refundTxnId;

    private String originalTransactionId;

    private String triggeredBy; // Admin or User ID

    private String remarks;

    private String ipAddress;

    private LocalDateTime createdAt;

}
