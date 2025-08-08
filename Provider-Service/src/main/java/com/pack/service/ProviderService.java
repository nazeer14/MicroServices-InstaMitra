package com.pack.service;


import com.pack.common.dto.ProviderRequestDTO;
import com.pack.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProviderService {
    Provider validateAndAdd(String phone);

    Provider findByPhone(String phone);

    Provider getById(Long id);

    List<Provider> getAll();

    void lockProvider(Long id, String reason);

    void unlockProvider(Long id);

    Page<Provider> getAllPaged(Pageable pageable);

    Page<Provider> getByOnlineStatus(boolean online, Pageable pageable);

    Provider updateProvider(Long id, ProviderRequestDTO dto);

    void deleteProvider(Long id);

    Provider updateStatus(Long id, boolean isOnline);

    String enableProvider(Long id, boolean isEnable);

    List<Provider> getByServiceId(String serviceId);
}

