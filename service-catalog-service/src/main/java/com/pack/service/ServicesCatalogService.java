package com.pack.service;

import com.pack.dto.PaginatedResponse;
import com.pack.dto.ServicesDTO;
import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServicesCatalogService {

    ServiceCatalog getById(Long id);

    ServiceCatalog addService(ServiceCatalog newService);

    ServiceCatalog updateService(Long id,ServicesDTO dto);

    void enableService(Long id,boolean isEnable);

    ServiceCatalog getByCode(String code);

    ServiceCatalog getByName(String name);

    String addSubService(Long id, List<SubService> subServices);

    List<ServiceCatalog> getAllServices();

    void deleteService(Long id);

    PaginatedResponse<SubService> getAvailableSubServices(String code,int page, int size, Sort sort);

}
