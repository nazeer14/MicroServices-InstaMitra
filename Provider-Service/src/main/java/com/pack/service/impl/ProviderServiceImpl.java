package com.pack.service.impl;

import com.pack.common.dto.Gender;
import com.pack.common.dto.ProviderRequestDTO;
import com.pack.dto.FormDetails;
import com.pack.entity.Provider;
import com.pack.enums.ProviderType;
import com.pack.enums.VerificationStatus;
import com.pack.repository.ProviderRepository;
import com.pack.service.ProviderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    // ✅ Create or Validate Provider (Evict cache to avoid stale data)
    @Override
    @CacheEvict(value = "providers", allEntries = true)
    public Provider validateAndAdd(String phoneNumber) {
        Optional<Provider> provider = providerRepository.findByPhoneNumber(phoneNumber);
        if (provider.isPresent()) {
            Provider provider1 = provider.get();
            provider1.setVerified(true);
            return providerRepository.save(provider1);
        }
        Provider provider1 = new Provider();
        provider1.setPhoneNumber(phoneNumber);
        provider1.setVerified(true);
        return providerRepository.save(provider1);
    }

    // ✅ Cache provider by phone
    @Override
    @Cacheable(value = "providers", key = "'phone:' + #phone")
    public Provider findByPhone(String phone) {
        return providerRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with Number " + phone + " not found"));
    }

    // ✅ Cache provider by ID
    @Override
    @Cacheable(value = "providers", key = "'id:' + #id")
    public Provider getById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with ID " + id + " not found"));
    }

    // ✅ Cache all providers
    @Override
    @Cacheable(value = "providers", key = "'all'")
    public List<Provider> getAll() {
        return providerRepository.findAll();
    }

    // ✅ Submit details → Update cache
    @Override
    @CachePut(value = "providers", key = "'id:' + #id")
    public Provider submitDetails(Long id, FormDetails dto) {
        Provider provider = getById(id);

        provider.setFullName(dto.getFullName());
        provider.setIndustryType(dto.getIndustryType());
        provider.setProfileImageUrl(dto.getProfileImageUrl());
        provider.setGender(dto.getGender());
        provider.setLicenseNumber(dto.getLicenseNumber());
        provider.setAge(dto.getAge());
        provider.setSubmitted(true);
        provider.setServicesOffered(dto.getServicesOffered());
        provider.setServiceIds(dto.getServiceIds());
        provider.setTeamSize(dto.getTeamSize());
        provider.setLocation(dto.getLocation());
        provider.setVerificationStatus(VerificationStatus.PROCESSING);

        return providerRepository.save(provider);
    }

    // ✅ Enable / Disable provider → Evict cache
    @Override
    @CacheEvict(value = "providers", key = "'id:' + #id")
    public String enableProvider(Long id, boolean isEnable) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with " + id + " not found"));
        if (provider.isEnabled() == isEnable) {
            return "Not updatable";
        }
        provider.setEnabled(isEnable);
        providerRepository.save(provider);
        return "Status updated";
    }

    // ✅ Cache providers by service ID
    @Override
    @Cacheable(value = "providers", key = "'service:' + #serviceId")
    public List<Provider> getByServiceId(String serviceId) {
        return providerRepository.findByServiceIdsContainsAndIsActiveTrueAndIsEnabledTrueAndIsLockedFalseAndIsVerifiedTrueAndIsSubmittedTrue(serviceId);
    }

    // ✅ Update verification → Evict cache
    @Override
    @CacheEvict(value = "providers", key = "'id:' + #id")
    public void setVerify(Long id, boolean isVerify) {
        Provider provider = getById(id);
        provider.setVerified(isVerify);
        providerRepository.save(provider);
    }

    // ✅ Update provider (full details) → Update cache
    @Override
    @CachePut(value = "providers", key = "'id:' + #id")
    @Transactional
    public Provider updateProvider(Long id, ProviderRequestDTO dto) {
        Provider provider = getById(id);

        provider.setFullName(dto.getFullName());
        provider.setEmail(dto.getEmail());
        provider.setGender(Gender.valueOf(dto.getGender()));
        provider.setAge(dto.getAge());
        provider.setProfileImageUrl(dto.getProfileImageUrl());
        provider.setLocation(dto.getLocation());
        provider.setProviderType(ProviderType.valueOf(dto.getProviderType()));
        provider.setIndustryType(dto.getIndustryType());
        provider.setServicesOffered(dto.getServicesOffered());
        provider.setServiceIds(dto.getServiceIds());
        provider.setExperienceInYears(dto.getExperienceInYears());
        provider.setHourlyRate(dto.getHourlyRate());
        provider.setTeamSize(dto.getTeamSize());
        provider.setLicenseNumber(dto.getLicenseNumber());

        return providerRepository.save(provider);
    }

    // ✅ Lock provider → Evict cache
    @Override
    @CacheEvict(value = "providers", key = "'id:' + #id")
    public void lockProvider(Long id, String reason) {
        Provider provider = getById(id);
        provider.setLocked(true);
        provider.setReasonForLock(reason);
        providerRepository.save(provider);
    }

    // ✅ Unlock provider → Evict cache
    @Override
    @CacheEvict(value = "providers", key = "'id:' + #id")
    public void unlockProvider(Long id) {
        Provider provider = getById(id);
        provider.setLocked(false);
        provider.setReasonForLock(null);
        providerRepository.save(provider);
    }

    @Override
    public Page<Provider> getAllPaged(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Provider> getByOnlineStatus(boolean online, Pageable pageable) {
        return null;
    }

    // ✅ Delete provider (soft delete) → Evict cache
    @Override
    @CacheEvict(value = "providers", key = "'id:' + #id")
    public void deleteProvider(Long id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider with ID " + id + " not found"));
        provider.setLocked(true);
        providerRepository.save(provider);
    }

    // ✅ Update online/offline → Update cache
    @Override
    @CachePut(value = "providers", key = "'id:' + #id")
    public Provider updateStatus(Long id, boolean isOnline) {
        Provider provider = getById(id);
        provider.setIsOnline(isOnline);
        if (isOnline) {
            provider.setLastLoginAt(LocalDateTime.now());
        } else {
            provider.setLastLogoutAt(LocalDateTime.now());
        }
        return providerRepository.save(provider);
    }
}
