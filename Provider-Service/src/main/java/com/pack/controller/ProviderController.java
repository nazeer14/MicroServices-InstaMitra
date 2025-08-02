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
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/provider/v1")
@RequiredArgsConstructor
@Tag(name = "Provider Management", description = "Endpoints for provider operations")
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping("/{id}/get-provider")
    @Operation(summary = "Get provider by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVIDER')")
    public ResponseEntity<ProviderResponseDTO> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(ProviderMapper.toDto(providerService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Get all providers (paginated)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProviderResponseDTO>> getAll(
            @ParameterObject Pageable pageable,
            @RequestParam(required = false) Boolean online
    ) {
        Page<Provider> page = (online == null)
                ? providerService.getAllPaged(pageable)
                : providerService.getByOnlineStatus(online, pageable);

        Page<ProviderResponseDTO> dtoPage = page.map(ProviderMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update provider profile")
    @PreAuthorize("hasRole('PROVIDER')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> lock(@PathVariable("id") Long id, @RequestParam("reason") String reason) {
        providerService.lockProvider(id, reason);
        return ResponseEntity.ok("Provider locked");
    }

    @PostMapping("/unlock/{id}")
    @Operation(summary = "Unlock a provider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unlock(@PathVariable("id") Long id) {
        providerService.unlockProvider(id);
        return ResponseEntity.ok("Provider unlocked");
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete a provider")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.ok("Provider deleted");
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update provider online/offline status")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ProviderResponseDTO> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("is_online") boolean isOnline) {
        return ResponseEntity.ok(ProviderMapper.toDto(providerService.updateStatus(id, isOnline)));
    }
}
