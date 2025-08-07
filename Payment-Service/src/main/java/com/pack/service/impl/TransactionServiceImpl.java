package com.pack.service.impl;

import com.pack.common.enums.PaymentStatus;
import com.pack.dto.RefundRequestDTO;
import com.pack.dto.RefundResponseDTO;
import com.pack.dto.TransactionRequestDTO;
import com.pack.dto.TransactionResponseDTO;
import com.pack.entity.RefundTransaction;
import com.pack.entity.Transaction;
import com.pack.exception.ResourceNotFoundException;
import com.pack.repository.RefundRepository;
import com.pack.repository.TransactionRepository;
import com.pack.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final RefundRepository refundRepository;

    @Override
    public TransactionResponseDTO createTransaction(TransactionRequestDTO dto) {
        Transaction transaction = Transaction.builder()
                .orderId(dto.getBookingId())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .transactionId(dto.getTransactionId())
                .paymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : PaymentStatus.PENDING)
                .transactionDate(LocalDateTime.now())
                .remarks(dto.getRemarks())
                .isRefunded(false)
                .build();

        return toDTO(transactionRepository.save(transaction));
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponseDTO getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    @Override
    public TransactionResponseDTO refundTransaction(String transactionId, String refundTransactionId, String remarks) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setIsRefunded(true);
        transaction.setRefundTransactionId(refundTransactionId);
        transaction.setRefundDate(LocalDateTime.now());
        transaction.setPaymentStatus(PaymentStatus.REFUNDED);
        transaction.setRemarks((remarks != null ? remarks : "") + " (Refunded)");

        return toDTO(transactionRepository.save(transaction));
    }

    @Override
    public TransactionResponseDTO findByTransactionId(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return toDTO(transaction);
    }

    @Override
    public TransactionResponseDTO findByRefundTransactionId(String refundTransactionId) {
        Transaction transaction = transactionRepository.findByRefundTransactionId(refundTransactionId)
                .orElseThrow(() -> new RuntimeException("Refund transaction not found"));
        return toDTO(transaction);
    }

    @Override
    @Transactional
    public RefundResponseDTO processRefund(RefundRequestDTO dto,Long userId, String ipAddress) {
        Transaction txn = transactionRepository.findByTransactionId(dto.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (txn.getPaymentStatus() != PaymentStatus.PAID) {
            throw new IllegalStateException("Refund can only be applied to successful transactions");
        }


        // Save refund transaction
        RefundTransaction refund = new RefundTransaction();
        refund.setRefundTxnId("refundTxnId");
        refund.setOriginalTransactionId(txn.getTransactionId());
        refund.setIpAddress(ipAddress);
        refund.setTriggeredBy(userId.toString());
        refund.setRemarks(dto.getReason());
        refund.setCreatedAt(LocalDateTime.now());
        refundRepository.save(refund);
        // Update original transaction
        txn.setPaymentStatus(PaymentStatus.REFUNDED);
        txn.setRefundTransactionId("refundTxnId");
        txn.setRefundDate(LocalDateTime.now());
        transactionRepository.save(txn);

        return new RefundResponseDTO("refundTxnId", "SUCCESS", "User requested refund");
    }


    private TransactionResponseDTO toDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .transactionId(transaction.getTransactionId())
                .bookingId(transaction.getOrderId())
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .transactionDate(transaction.getTransactionDate())
                .paymentStatus(transaction.getPaymentStatus())
                .isRefunded(transaction.getIsRefunded())
                .refundTransactionId(transaction.getRefundTransactionId())
                .refundDate(transaction.getRefundDate())
                .remarks(transaction.getRemarks())
                .build();
    }
}
