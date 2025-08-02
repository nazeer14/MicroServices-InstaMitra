package com.pack.service;


import com.pack.common.dto.ProviderRequestDTO;
import com.pack.entity.Provider;
import com.pack.enums.ProviderType;
import com.pack.exception.ProviderNotFoundException;
import com.pack.repository.ProviderRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService{

    private final ProviderRepository providerRepository;


    @Override
    public Provider validateAndAdd(String phoneNumber) {
       Optional<Provider> provider= providerRepository.findByPhoneNumber(phoneNumber);
       if(provider.isPresent())
       {
           Provider provider1=provider.get();
           provider1.setVerified(true);
           return providerRepository.save(provider1);
       }
       Provider provider1=new Provider();
       provider1.setPhoneNumber(phoneNumber);
       provider1.setVerified(true);
       return providerRepository.save(provider1);
    }
    @Override
    public Provider findByPhone(String phone) {
        return providerRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new ProviderNotFoundException("Provider with Number "+phone+" not found"));
    }

    @Override
    public Provider getById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ProviderNotFoundException("Provider with ID "+id+" not found"));
    }

    @Override
    public List<Provider> getAll() {
        return providerRepository.findAll();
    }

    @Override
    public String enableProvider(Long id,boolean isEnable){
        Optional<Provider> provider=providerRepository.findById(id);
        if(provider.isEmpty()){
            throw new ProviderNotFoundException("Provider with "+id+" not found");
        }
        Provider newProvider=provider.get();
        if(newProvider.isEnabled() == isEnable){
            return "Not updatable ";
        }
        newProvider.setEnabled(isEnable);
        providerRepository.save(newProvider);
        return "Status updated";
    }

    @Override
    public Page<Provider> getAllPaged(Pageable pageable) {
        return providerRepository.findAll(pageable);
    }

    @Override
    public Page<Provider> getByOnlineStatus(boolean online, Pageable pageable) {
        return providerRepository.findByIsOnline(online, pageable);
    }

    @Override
    @Transactional
    public Provider updateProvider(Long id, ProviderRequestDTO dto) {
        Provider provider = getById(id);

        provider.setFullName(dto.getFullName());
        provider.setEmail(dto.getEmail());
        provider.setGender(dto.getGender());
        provider.setAge(dto.getAge());
        provider.setProfileImageUrl(dto.getProfileImageUrl());
        provider.setLocation(dto.getLocation());
        provider.setProviderType(ProviderType.valueOf(dto.getProviderType()));
        provider.setIndustryType(dto.getIndustryType());
        provider.setServicesOffered(dto.getServicesOffered());
        provider.setExperienceInYears(dto.getExperienceInYears());
        provider.setHourlyRate(dto.getHourlyRate());
        provider.setTeamSize(dto.getTeamSize());
        provider.setLicenseNumber(dto.getLicenseNumber());

        return providerRepository.save(provider);
    }


    @Override
    public void lockProvider(Long id, String reason) {
        Provider provider = getById(id);
        provider.setLocked(true);
        provider.setReasonForLock(reason);
        providerRepository.save(provider);
    }

    @Override
    public void unlockProvider(Long id) {
        Provider provider = getById(id);
        provider.setLocked(false);
        provider.setReasonForLock(null);
        providerRepository.save(provider);
    }

    @Override
    public void deleteProvider(Long id) {
        Optional<Provider> provider=providerRepository.findById(id);
        if(provider.isEmpty()){
            throw new RuntimeException("Invalid Id");
        }
        Provider provider1=provider.get();
        provider1.setLocked(true);
        providerRepository.save(provider1);
    }

    @Override
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
