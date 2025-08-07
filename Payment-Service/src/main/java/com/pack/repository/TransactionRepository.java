package com.pack.repository;


import com.pack.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    boolean existsByTransactionId(String transactionId);
    Optional<Transaction> findByTransactionId(String transactionId);
    Optional<Transaction> findByRefundTransactionId(String refundTransactionId);

}
