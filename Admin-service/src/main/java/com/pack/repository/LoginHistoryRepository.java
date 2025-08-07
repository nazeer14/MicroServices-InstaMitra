package com.pack.repository;

import com.pack.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory,Long> {
    List<LoginHistory> findByAdminId(Long adminId);
}
