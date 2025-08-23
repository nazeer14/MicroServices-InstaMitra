package com.pack.controller;

import com.pack.common.response.ApiResponse;
import com.pack.dto.PaginatedResponse;
import com.pack.dto.ServicesDTO;
import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;
import com.pack.service.ServicesCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/service")
@RequiredArgsConstructor
@Tag(name = "Service Catalog", description = "APIs for managing services and sub-services")
public class ServiceCatalogController {

    private final ServicesCatalogService servicesCatalogService;

    @Operation(summary = "Create a new service")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ServiceCatalog>> createService(@Valid @RequestBody ServicesDTO dto) {
        ServiceCatalog newService=new ServiceCatalog();
        newService.setName(dto.getName());
        newService.setAbout(dto.getAbout());
        newService.setServiceCategory(dto.getCategory());
        ServiceCatalog created = servicesCatalogService.addService(newService);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Service created successfully", created));
    }

    @Operation(summary = "Get service by ID")
    @GetMapping("/{id}/get")
    public ResponseEntity<ApiResponse<ServiceCatalog>> getServiceById(@PathVariable Long id) {
        ServiceCatalog service = servicesCatalogService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("Service fetched successfully", service));
    }

    @Operation(summary = "Update an existing service")
    @PutMapping("/{id}/update")
    public ResponseEntity<ApiResponse<ServiceCatalog>> updateService(@PathVariable Long id,
                                                                     @Valid @RequestBody ServicesDTO dto) {
        ServiceCatalog updated = servicesCatalogService.updateService(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Service updated successfully", updated));
    }

    @Operation(summary = "Enable/Disable a service")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateServiceStatus(@PathVariable Long id,
                                                                 @RequestParam boolean enabled) {
        servicesCatalogService.enableService(id, enabled);
        return ResponseEntity.ok(ApiResponse.ok("Service " + (enabled ? "enabled" : "disabled"), null));
    }

    @Operation(summary = "Get paginated sub-services")
    @GetMapping("/{serviceId}/subservices")
    public ResponseEntity<ApiResponse<PaginatedResponse<SubService>>> getSubServices(
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort) {
        PaginatedResponse<SubService> result = servicesCatalogService.getAvailableSubServices(serviceId,page,size, Sort.by(sort));
        return ResponseEntity.ok(ApiResponse.ok("Sub-services fetched successfully", result));
    }

    @Operation(summary = "get all services")
    @GetMapping("/get-all")
    public ResponseEntity<ApiResponse<?>> getAllServices(){
        List<ServiceCatalog> result= servicesCatalogService.getAllServices();
        return ResponseEntity.ok(ApiResponse.ok("getting all services", result));
    }
}
