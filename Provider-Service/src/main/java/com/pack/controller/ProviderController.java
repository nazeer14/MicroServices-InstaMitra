package com.pack.controller;

import com.pack.common.dto.ProviderRequestDTO;
import com.pack.common.dto.ProviderResponseDTO;
import com.pack.entity.Provider;
import com.pack.service.ProviderService;
import com.pack.utils.ProviderMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/provider/v1")
@RequiredArgsConstructor
@Tag(name = "Provider Management", description = "Endpoints for provider operations")
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping("/{id}/get-provider")
    @Operation(summary = "Get provider by ID")
    public ResponseEntity<ProviderResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ProviderMapper.toDto(providerService.getById(id)));
    }

    @GetMapping("/{id}/get")
    @Operation(summary = "Get provider by service ID")
    public ResponseEntity<?> getByServiceId(@PathVariable("id") String serviceId) {
        List<ProviderResponseDTO> result= providerService.getByServiceId(serviceId)
                .stream()
                .map(ProviderMapper::toDto)
                .toList();

        return ResponseEntity.ok(result);

    }

    @GetMapping
    @Operation(summary = "Get all providers (paginated)")
    public ResponseEntity<?> getAll(
            Pageable pageable,
            @RequestParam(value = "isActive", required = false) Boolean isActive) {

        Page<Provider> page = (isActive == null)
                ? providerService.getAllPaged(pageable)
                : providerService.getByOnlineStatus(isActive, pageable);

        Page<ProviderResponseDTO> dtoPage = page.map(ProviderMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }


    @PutMapping("/{id}/update")
    @Operation(summary = "Update provider profile")
    public ResponseEntity<ProviderResponseDTO> update(
            @PathVariable("id") Long id,
            @RequestBody @Valid ProviderRequestDTO request) {
        Provider updated = providerService.updateProvider(id, request);
        return ResponseEntity.ok(ProviderMapper.toDto(updated));
    }

    @PostMapping("/enable/{id}")
    @Operation(summary = "is Enable for Service")
    public ResponseEntity<?> enableForService(@PathVariable("id") Long id,@RequestParam("isEnable") boolean isEnable){       String msg=providerService.enableProvider(id,isEnable);
        return ResponseEntity.ok(msg + (isEnable ? " You are now enabled for service." : " You are now disabled for service."));
    }

    @PostMapping("/lock/{id}")
    @Operation(summary = "Lock a provider")
    public ResponseEntity<String> lock(@PathVariable("id") Long id, @RequestParam("reason") String reason) {
        providerService.lockProvider(id, reason);
        return ResponseEntity.ok("Provider locked");
    }

    @PostMapping("/unlock/{id}")
    @Operation(summary = "Unlock a provider")
    public ResponseEntity<String> unlock(@PathVariable("id") Long id) {
        providerService.unlockProvider(id);
        return ResponseEntity.ok("Provider unlocked");
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a provider")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.ok("Provider deleted");
    }

    @PatchMapping("/{id}/online_status")
    @Operation(summary = "Update provider online/offline status")
    public ResponseEntity<ProviderResponseDTO> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("is_online") boolean isOnline) {
        return ResponseEntity.ok(ProviderMapper.toDto(providerService.updateStatus(id, isOnline)));
    }


}
