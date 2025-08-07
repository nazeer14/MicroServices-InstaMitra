package com.pack.repository;

import com.pack.entity.RefundTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefundRepository extends JpaRepository<RefundTransaction, Long> {
}
