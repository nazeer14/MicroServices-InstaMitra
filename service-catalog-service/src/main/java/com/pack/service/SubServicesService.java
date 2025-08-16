package com.pack.service;

import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SubServicesService {

    SubService getById(Long id);

    SubService getByName(String name);

    String addSubService(Long id, List<SubService> subService);

    void deleteSubService(Long id);

    void updateService(SubService subService);

    List<SubService> getByServiceId(Long id);

    SubService getByCode(String code);

    Page<SubService> getAvailableSubServices(Pageable pageable);

    Page<SubService> getAvailableSubServicesByServiceId(String serviceId, Pageable pageable);
}
