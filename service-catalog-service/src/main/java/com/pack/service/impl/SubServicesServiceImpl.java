package com.pack.service.impl;

import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;
import com.pack.repository.ServiceRepository;
import com.pack.repository.SubServiceRepository;
import com.pack.service.SubServicesService;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubServicesServiceImpl implements SubServicesService {

    private final SubServiceRepository subServiceRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public SubService getById(Long id) {
        return subServiceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("SubService not found with id: " + id));
    }

    @Override
    public SubService getByName(String name) {
        return subServiceRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("SubService not found with name: " + name));
    }

    @Override
    @Transactional
    public String addSubService(Long serviceId, List<SubService> subServices) {
        ServiceCatalog serviceCatalog = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        for (SubService sub : subServices) {
            sub.setService(serviceCatalog);
            SubService saved = subServiceRepository.save(sub);
            String subCode = String.format("SS-%03d", saved.getId());
            saved.setCode(subCode);
            subServiceRepository.save(saved);
        }

        return "Sub-services added with codes";
    }


    @Override
    public void deleteSubService(Long id) {
        SubService subService = getById(id);
        subServiceRepository.delete(subService);
    }

    @Override
    public void updateService(SubService updatedSubService) {
        SubService existing = getById(updatedSubService.getId());
        existing.setName(updatedSubService.getName());
        existing.setDescription(updatedSubService.getDescription());
        existing.setItIsAvailable(updatedSubService.isItIsAvailable());
        subServiceRepository.save(existing);
    }

    @Override
    public List<SubService> getByServiceId(Long serviceId) {
        return subServiceRepository.findAllByServiceId(serviceId);
    }


    // SubServiceServiceImpl.java
    @Override
    public SubService getByCode(String code) {
        return subServiceRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Sub-service not found with code: " + code));
    }


    @Override
    public Page<SubService> getAvailableSubServices(Pageable pageable) {
        return subServiceRepository.findAllByItIsAvailable(true, pageable);
    }

    @Override
    public Page<SubService> getAvailableSubServicesByServiceId(Long serviceId, Pageable pageable) {
        return subServiceRepository.findAllByServiceIdAndItIsAvailable(serviceId, true, pageable);
    }


}

