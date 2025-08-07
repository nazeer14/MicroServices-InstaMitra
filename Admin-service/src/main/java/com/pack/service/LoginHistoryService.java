package com.pack.service;

import com.pack.entity.LoginHistory;

import java.util.List;

public interface LoginHistoryService {

    LoginHistory saveLogin(Long adminId);

    List<LoginHistory> getLoginHistoryByAdmin(Long adminId);

    List<LoginHistory> getAllLogins();
}
