package com.pack.controller;

import com.pack.common.response.ApiResponse;
import com.pack.dto.PaginatedResponse;
import com.pack.dto.SubServiceDTO;
import com.pack.entity.SubService;
import com.pack.service.SubServicesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/subservice")
@RequiredArgsConstructor
@Tag(name = "Sub Services", description = "Endpoints for managing Sub Services")
public class SubServiceController {

    private final SubServicesService subServicesService;

    @Operation(summary = "Get SubService by ID")
    @GetMapping("/{id}")
    public ResponseEntity<SubService> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subServicesService.getById(id));
    }

    @Operation(summary = "Get SubService by name")
    @GetMapping("/by-name/{name}")
    public ResponseEntity<SubService> getByName(@PathVariable String name) {
        return ResponseEntity.ok(subServicesService.getByName(name));
    }

    @Operation(summary = "Add multiple SubServices using SubService entities")
    @PostMapping("/add/{serviceId}")
    public ResponseEntity<String> addSubServices(
            @PathVariable Long serviceId,
            @RequestBody List<@Valid SubService> subServices
    ) {
        return ResponseEntity.ok(subServicesService.addSubService(serviceId, subServices));
    }

    @Operation(summary = "Add multiple SubServices using DTOs")
    @PostMapping("/{id}/sub-services")
    public ResponseEntity<String> addSubService(@PathVariable Long id, @RequestBody List<SubServiceDTO> dtos) {
        List<SubService> entities = dtos.stream()
                .map(dto -> {
                    SubService sub = new SubService();
                    sub.setName(dto.getName());
                    return sub;
                }).collect(Collectors.toList());

        return ResponseEntity.ok(subServicesService.addSubService(id, entities));
    }

    @Operation(summary = "Get SubService by code")
    @GetMapping("/code/{code}")
    public ResponseEntity<SubService> getSubServiceByCode(@PathVariable String code) {
        return ResponseEntity.ok(subServicesService.getByCode(code));
    }

    @Operation(summary = "Get paginated list of available SubServices with sorting")
    @GetMapping("/available")
    public ResponseEntity<PaginatedResponse<SubService>> getAvailableSubServices(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String[] sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<SubService> resultPage = subServicesService.getAvailableSubServices(pageable);

        PaginatedResponse<SubService> response = new PaginatedResponse<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get paginated list of available SubServices by Service ID")
    @GetMapping("/available/by-service/{serviceId}")
    public ResponseEntity<Page<SubService>> getAvailableSubServicesByServiceId(
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(subServicesService.getAvailableSubServicesByServiceId(serviceId, pageable));
    }

    @Operation(summary = "Update an existing SubService")
    @PutMapping("/update")
    public ResponseEntity<String> updateSubService(@Valid @RequestBody SubService subService) {
        subServicesService.updateService(subService);
        return ResponseEntity.ok("SubService updated successfully");
    }

    @Operation(summary = "Delete SubService by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubService(@PathVariable Long id) {
        subServicesService.deleteSubService(id);
        return ResponseEntity.ok("SubService deleted successfully");
    }

    @Operation(summary = "Get all SubServices by parent Service ID")
    @GetMapping("/by-service/{serviceId}")
    public ResponseEntity<List<SubService>> getByServiceId(@PathVariable Long serviceId) {
        return ResponseEntity.ok(subServicesService.getByServiceId(serviceId));
    }

    // helper method to parse sort parameters
    private List<Sort.Order> getSortOrders(String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();
        for (String sortParam : sort) {
            String[] sortPair = sortParam.split(",");
            if (sortPair.length == 2) {
                String field = sortPair[0];
                String direction = sortPair[1];
                orders.add(new Sort.Order(
                        direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                        field
                ));
            } else if (sortPair.length == 1) {
                orders.add(new Sort.Order(Sort.Direction.ASC, sortPair[0]));
            }
        }
        return orders;
    }

    @Operation(summary = "get all service by id")
    @GetMapping("/services/{serviceId}/available-subservices")
    public ResponseEntity<ApiResponse<?>> getAvailableSubServicesByServiceId(
            @PathVariable Long serviceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);
        Page<SubService> resultPage = subServicesService.getAvailableSubServicesByServiceId(serviceId, pageable);

        PaginatedResponse<SubService> response = new PaginatedResponse<>(
                resultPage.getContent(),
                resultPage.getNumber(),
                resultPage.getSize(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
        );

        return ResponseEntity.ok(ApiResponse.ok("success", response));
    }
}
