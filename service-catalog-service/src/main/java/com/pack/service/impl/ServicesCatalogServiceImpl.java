package com.pack.service.impl;

import com.pack.dto.PaginatedResponse;
import com.pack.dto.ServicesDTO;
import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;
import com.pack.repository.ServiceRepository;
import com.pack.repository.SubServiceRepository;
import com.pack.service.ServicesCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ServicesCatalogServiceImpl implements ServicesCatalogService {

    private final ServiceRepository serviceRepository;
    private final SubServiceRepository subServiceRepository;

    @Override
    @Cacheable(value = "serviceById", key = "#id")
    public ServiceCatalog getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Service not found with ID: " + id));
    }

    @Override
    @CachePut(value = "serviceById", key = "#result.id")
    public ServiceCatalog addService(ServiceCatalog newService) {

        ServiceCatalog saved = serviceRepository.save(newService);
        String serviceCode = String.format("S-%03d", saved.getId());
        saved.setServiceCode(serviceCode);

        return serviceRepository.save(saved);
    }


    @Override
    @CachePut(value = "serviceById", key = "#id")
    public ServiceCatalog updateService(Long id, ServicesDTO updatedService) {
        ServiceCatalog existing = getById(id);
        existing.setName(updatedService.getName());
        existing.setServiceCategory(updatedService.getCategory());
        existing.setAbout(updatedService.getAbout());

        return serviceRepository.save(existing);
    }

    @Override
    public void enableService(Long id, boolean isEnable) {

    }

    @Override
    @CacheEvict(value = "serviceById", key = "#id")
    public void deleteService(Long id) {
        ServiceCatalog service = getById(id);
        serviceRepository.delete(service);
    }

    @Override
    @Cacheable(value = "serviceByCode", key = "#code")
    public ServiceCatalog getByCode(String code) {
        return serviceRepository.findByServiceCode(code)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Service not found with code: " + code));
    }

    @Override
    @Cacheable(value = "serviceByName", key = "#name")
    public ServiceCatalog getByName(String name) {
        return serviceRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Service not found with name: " + name));
    }


    @Override
    public List<ServiceCatalog> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    @Cacheable(value = "subServices", key = "#serviceCode + '_' + #page + '_' + #size + '_' + #sort.toString()")
    public PaginatedResponse<SubService> getAvailableSubServices(Long serviceId, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubService> subServicePage =
                subServiceRepository.findAllByServiceIdAndItIsAvailable(serviceId, true, pageable);

        return new PaginatedResponse<>(
                subServicePage.getContent(),
                subServicePage.getNumber(),
                subServicePage.getSize(),
                subServicePage.getTotalElements(),
                subServicePage.getTotalPages()
        );
    }
}
