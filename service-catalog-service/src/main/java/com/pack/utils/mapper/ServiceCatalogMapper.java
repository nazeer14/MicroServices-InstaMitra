package com.pack.utils.mapper;

import com.pack.dto.ServicesDTO;
import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;

import java.util.List;
import java.util.stream.Collectors;

public class ServiceCatalogMapper {

    public static ServiceCatalog toEntity(ServicesDTO dto) {
        if (dto == null) return null;

        ServiceCatalog service = new ServiceCatalog();
        service.setName(dto.getName());
        service.setServiceCategory(dto.getCategory());
        service.setAbout(dto.getAbout());

        if (dto.getSubServices() != null) {
            List<SubService> subEntities = dto.getSubServices().stream()
                    .map(subDto -> {
                        SubService sub = new SubService();
                        sub.setId(subDto.getId());
                        sub.setName(subDto.getName());
                        sub.setService(service);
                        return sub;
                    })
                    .collect(Collectors.toList());
            service.setSubServices(subEntities);
        }

        return service;
    }

    public static ServicesDTO toDTO(ServiceCatalog service) {
        if (service == null) return null;

        ServicesDTO dto = new ServicesDTO();
        dto.setName(service.getName());
        dto.setCategory(service.getServiceCategory());
        dto.setAbout(service.getAbout());

        return dto;
    }
}
