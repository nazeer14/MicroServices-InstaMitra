package com.pack.service;

import com.pack.dto.TransactionRequestDTO;
import com.pack.dto.TransactionResponseDTO;
import com.pack.entity.Transaction;
import com.pack.repository.TransactionRepository;
import com.pack.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction transaction;
    private String transactionId;
    private final Long userId = 123456789L;
    private final Long workerId = 987654321L;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        transaction = Transaction.builder()
                .id(1000000L)
                .transactionId("transactionId")
                .amount(BigDecimal.valueOf(500))
                .paymentStatus(PaymentStatus.SUCCESS)
                .paymentMethod("UPI")
                .userId(userId)
                .workerId(workerId)
                .transactionDate(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateTransaction() {
        TransactionRequestDTO request = TransactionRequestDTO.builder()
                .amount(BigDecimal.valueOf(500))
                .paymentMethod("UPI")
                .paymentStatus(PaymentStatus.SUCCESS)
                .userId(userId)
                .workerId(workerId)
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionResponseDTO response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(transaction.getAmount(), response.getAmount());
        assertEquals(transaction.getPaymentStatus(), response.getPaymentStatus());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testGetAllTransactions() {
        when(transactionRepository.findAll()).thenReturn(List.of(transaction));

        List<TransactionResponseDTO> result = transactionService.getAllTransactions();

        assertEquals(1, result.size());
        assertEquals(transaction.getId(), result.get(0).getId());
    }

    @Test
    void testGetTransactionById_Success() {
        when(transactionRepository.findByTransactionId(transactionId)).thenReturn(Optional.of(transaction));

        TransactionResponseDTO result = transactionService.findByTransactionId(transactionId);

        assertNotNull(result);
        assertEquals(transactionId, result.getTransactionId());
    }

    @Test
    void testGetTransactionById_NotFound() {

        when(transactionRepository.findByTransactionId("notFoundId")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> transactionService.findByTransactionId("notFoundId"));
    }

    @Test
    void testGetAllUserTransactions() {
        Long userId = transaction.getUserId();
        when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));

        List<TransactionResponseDTO> result = transactionService.getAllUserTransactions(userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
    }

    @Test
    void testGetAllWorkerTransactions() {
        Long workerId = transaction.getWorkerId();
        when(transactionRepository.findByWorkerId(workerId)).thenReturn(List.of(transaction));

        List<TransactionResponseDTO> result = transactionService.getAllWorkerTransactions(workerId);

        assertEquals(1, result.size());
        assertEquals(workerId, result.get(0).getWorkerId());
    }

    @Test
    void testRefundTransaction_Success() {
        String remarks = "User requested refund";

        when(transactionRepository.findByTransactionId("transactionId")).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponseDTO response = transactionService.refundTransaction("transactionId", "refundTxnId", remarks);

        assertNotNull(response);
        assertEquals("refundTxnId", response.getRefundTransactionId());
        assertEquals(PaymentStatus.REFUNDED, response.getPaymentStatus());
        assertEquals("User requested refund (Refunded)", response.getRemarks());
    }
    @Test
    void testRefundTransaction_TransactionNotFound() {


        when(transactionRepository.findByTransactionId("nonExistentId")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                transactionService.refundTransaction("nonExistentId", "refundTxnId", "Refund Request"));
    }
    @Test
    void testFindByRefundTransactionId_Success() {
        transaction.setRefundTransactionId("refundTxnId");

        when(transactionRepository.findByRefundTransactionId("refundTxnId")).thenReturn(Optional.of(transaction));

        TransactionResponseDTO result = transactionService.findByRefundTransactionId("refundTxnId");

        assertNotNull(result);
        assertEquals("refundTxnId", result.getRefundTransactionId());
    }

}
