package com.pack.service.impl;

import com.pack.entity.Admin;
import com.pack.entity.LoginHistory;
import com.pack.repository.AdminRepository;
import com.pack.repository.LoginHistoryRepository;
import com.pack.service.LoginHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final AdminRepository adminRepository;

    @Override
    public LoginHistory saveLogin(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with ID: " + adminId));

        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setAdmin(admin);
        loginHistory.setLoginTime(LocalDateTime.now());

        log.info("Login recorded for admin ID: {}", adminId);
        return loginHistoryRepository.save(loginHistory);
    }

    @Override
    public List<LoginHistory> getLoginHistoryByAdmin(Long adminId) {
        return loginHistoryRepository.findByAdminId(adminId);
    }

    @Override
    public List<LoginHistory> getAllLogins() {
        return loginHistoryRepository.findAll();
    }
}
