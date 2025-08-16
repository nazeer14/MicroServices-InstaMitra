package com.pack.service.impl;

import com.pack.dto.PaginatedResponse;
import com.pack.dto.ServicesDTO;
import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;
import com.pack.repository.ServiceRepository;
import com.pack.repository.SubServiceRepository;
import com.pack.service.ServicesCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ServicesCatalogServiceImpl implements ServicesCatalogService {

    private final ServiceRepository serviceRepository;
    private final SubServiceRepository subServiceRepository;

    @Override
    public ServiceCatalog getById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Service not found with ID: " + id));
    }

    @Override
    public ServiceCatalog addService(ServiceCatalog newService) {
        if (newService.getSubServices() != null) {
            newService.getSubServices().forEach(sub -> sub.setService(newService));
        }
        ServiceCatalog saved = serviceRepository.save(newService);

        String serviceCode = String.format("S-%03d", saved.getId());
        saved.setServiceCode(serviceCode);

        return serviceRepository.save(saved);
    }

    @Override
    public ServiceCatalog updateService(Long id, ServicesDTO updatedService) {
        ServiceCatalog existing = getById(id);
        existing.setName(updatedService.getName());
        existing.setServiceCategory(updatedService.getCategory());
        existing.setAbout(updatedService.getAbout());

        if (updatedService.getSubServices() != null) {
            updatedService.getSubServices().forEach(sub -> sub.setService(existing));
            existing.setSubServices(updatedService.getSubServices());
        }
        return serviceRepository.save(existing);
    }

    @Override
    public void enableService(Long id, boolean isEnable) {
        ServiceCatalog service = getById(id);
        if (service.isEnabled() == isEnable) {
            throw new ResponseStatusException(BAD_REQUEST, "Service already has enabled=" + isEnable);
        }
        service.setEnabled(isEnable);
        serviceRepository.save(service);
    }

    @Override
    public ServiceCatalog getByCode(String code) {
        return serviceRepository.findByServiceCode(code)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Service not found with code: " + code));
    }

    @Override
    public ServiceCatalog getByName(String name) {
        return serviceRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Service not found with name: " + name));
    }

    @Override
    public String addSubService(Long id, List<SubService> subServices) {
        ServiceCatalog service = getById(id);

        subServices.forEach(sub -> sub.setService(service));

        if (service.getSubServices() != null) {
            service.getSubServices().addAll(subServices);
        } else {
            service.setSubServices(subServices);
        }

        subServiceRepository.saveAll(subServices);
        serviceRepository.save(service);

        return "Sub-services added successfully to Service ID: " + id;
    }

    @Override
    public List<ServiceCatalog> getAllServices() {
        return serviceRepository.findAll();
    }

    @Override
    public void deleteService(Long id) {
        ServiceCatalog service = getById(id);
        serviceRepository.delete(service);
    }

    @Override
    public PaginatedResponse<SubService> getAvailableSubServices(String serviceCode, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubService> subServicePage =
                subServiceRepository.findAllByServiceIdAndItIsAvailable(serviceCode, true, pageable);

        return new PaginatedResponse<>(
                subServicePage.getContent(),
                subServicePage.getNumber(),
                subServicePage.getSize(),
                subServicePage.getTotalElements(),
                subServicePage.getTotalPages()
        );
    }
}
