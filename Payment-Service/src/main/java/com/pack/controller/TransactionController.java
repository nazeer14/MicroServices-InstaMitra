package com.pack.controller;

import com.pack.dto.RefundRequestDTO;
import com.pack.dto.RefundResponseDTO;
import com.pack.dto.TransactionRequestDTO;
import com.pack.dto.TransactionResponseDTO;
import com.pack.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody @Valid TransactionRequestDTO dto) {
        return ResponseEntity.ok(transactionService.createTransaction(dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping("/refund/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> refundTransaction(
            @PathVariable("transactionId") String transactionId,
            @RequestParam("refundTransactionId") String refundTransactionId,
            @RequestParam(value = "remarks", required = false) String remarks
    ) {
        return ResponseEntity.ok(transactionService.refundTransaction(transactionId, refundTransactionId, remarks));
    }

    @GetMapping("/txn/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> getByTransactionId(
            @PathVariable("transactionId") String transactionId) {
        return ResponseEntity.ok(transactionService.findByTransactionId(transactionId));
    }

    @GetMapping("/refundTxn/{refundTransactionId}")
    public ResponseEntity<TransactionResponseDTO> getByRefundTransactionId(
            @PathVariable("refundTransactionId") String refundTransactionId) {
        return ResponseEntity.ok(transactionService.findByRefundTransactionId(refundTransactionId));
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/refund")
    public ResponseEntity<RefundResponseDTO> processSecureRefund(
            @RequestBody @Valid RefundRequestDTO request,
            HttpServletRequest httpRequest,
            Principal principal
    ) {
        Long userId =Long.parseLong(principal.getName());
        String ip = httpRequest.getRemoteAddr();

        RefundResponseDTO response = transactionService.processRefund(request, userId, ip);
        return ResponseEntity.ok(response);
    }


}
