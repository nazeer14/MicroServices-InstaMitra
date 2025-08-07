package com.pack.service;

import com.pack.dto.RefundRequestDTO;
import com.pack.dto.RefundResponseDTO;
import com.pack.dto.TransactionRequestDTO;
import com.pack.dto.TransactionResponseDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    TransactionResponseDTO createTransaction(TransactionRequestDTO dto);
    List<TransactionResponseDTO> getAllTransactions();
    TransactionResponseDTO getTransactionById(Long id);
    TransactionResponseDTO refundTransaction(String transactionId, String refundTransactionId, String remarks);

    TransactionResponseDTO findByTransactionId(String transactionId);

    TransactionResponseDTO findByRefundTransactionId(String refundTransactionId);

    RefundResponseDTO processRefund(RefundRequestDTO request, Long userId, String ip);
}
