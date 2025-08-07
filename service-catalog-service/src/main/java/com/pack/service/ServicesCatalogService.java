package com.pack.service;

import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServicesCatalogService {

    ServiceCatalog getById(Long id);

    ServiceCatalog addService(ServiceCatalog newService);

    void updateService(ServiceCatalog updatedService);

    void enableService(Long id,boolean isEnable);

    ServiceCatalog getByCode(String code);

    ServiceCatalog getByName(String name);

    String addSubService(Long id, List<SubService> subServices);

    List<ServiceCatalog> getAllServices();

    void deleteService(Long id);

    Page<SubService> getAvailableSubServices(Pageable pageable);
}
