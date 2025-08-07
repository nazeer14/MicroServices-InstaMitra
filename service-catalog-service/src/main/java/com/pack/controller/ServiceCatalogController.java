package com.pack.controller;

import com.pack.dto.ServicesDTO;
import com.pack.entity.ServiceCatalog;
import com.pack.entity.SubService;
import com.pack.dto.PaginatedResponse;
import com.pack.service.ServicesCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services/v1")
@RequiredArgsConstructor
@Tag(name = "Service Catalog", description = "APIs for managing services and sub-services")
public class ServiceCatalogController {

    private final ServicesCatalogService servicesCatalogService;

    @Operation(summary = "Health check endpoint")
    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from Service Catalog");
    }

    @Operation(summary = "Get service by ID")
    @GetMapping("/{id}/get")
    public ResponseEntity<ServiceCatalog> getService(@PathVariable("id") Long id) {
        ServiceCatalog service = servicesCatalogService.getById(id);
        return ResponseEntity.ok(service);
    }

    @Operation(summary = "Add new service")
    @PostMapping("/add")
    public ResponseEntity<ServiceCatalog> addService(@Valid @RequestBody ServicesDTO dto) {
        ServiceCatalog serviceCatalog = new ServiceCatalog();
        serviceCatalog.setName(dto.getName());
        serviceCatalog.setServiceCategory(dto.getCategory());
        serviceCatalog.setSubServices(dto.getSubServices());
        serviceCatalog.setEnabled(false);
        ServiceCatalog saved = servicesCatalogService.addService(serviceCatalog);
        return ResponseEntity.ok(saved);
    }

    @Operation(summary = "Enable or disable a service")
    @PutMapping("/{id}/enable")
    public ResponseEntity<String> enableService(@PathVariable("id") Long id, @RequestParam("status") boolean isEnable) {
        servicesCatalogService.enableService(id, isEnable);
        return ResponseEntity.ok("Service " + (isEnable ? "enabled" : "disabled") + " successfully.");
    }

    @Operation(summary = "Get service by code")
    @GetMapping("/code/{code}")
    public ResponseEntity<ServiceCatalog> getServiceByCode(@PathVariable("code") String code) {
        return ResponseEntity.ok(servicesCatalogService.getByCode(code));
    }

    @Operation(summary = "Get service by name")
    @GetMapping("/by-name")
    public ResponseEntity<ServiceCatalog> getServiceByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(servicesCatalogService.getByName(name));
    }

    @Operation(summary = "Update existing service")
    @PutMapping("/update")
    public ResponseEntity<String> updateService(@RequestBody ServiceCatalog updatedService) {
        servicesCatalogService.updateService(updatedService);
        return ResponseEntity.ok("Service updated");
    }

    @Operation(summary = "Get all services")
    @GetMapping("/all")
    public ResponseEntity<List<ServiceCatalog>> getAll() {
        return ResponseEntity.ok(servicesCatalogService.getAllServices());
    }

    @Operation(summary = "Delete service by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable("id") Long id) {
        servicesCatalogService.deleteService(id);
        return ResponseEntity.ok("Service deleted");
    }

    @Operation(summary = "Get paginated available sub-services")
    @GetMapping("/subservices/available")
    public ResponseEntity<PaginatedResponse<SubService>> getAvailableSubServices(
            @RequestParam(defaultValue = "0",name = "page") int page,
            @RequestParam(defaultValue = "10", name = "size") int size,
            @RequestParam(defaultValue = "name,asc" ,name = "sort") String[] sort
    ) {
        Sort sortObj = Sort.by(Sort.Order.by(sort[0])
                .with("desc".equalsIgnoreCase(sort[1]) ? Sort.Direction.DESC : Sort.Direction.ASC));

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<SubService> resultPage = servicesCatalogService.getAvailableSubServices(pageable);

        PaginatedResponse<SubService> response = new PaginatedResponse<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages(),
                resultPage.isLast()
        );

        return ResponseEntity.ok(response);
    }
}
