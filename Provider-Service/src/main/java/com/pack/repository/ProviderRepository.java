package com.pack.repository;

import com.pack.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProviderRepository extends JpaRepository<Provider,Long> {
    Optional<Provider> findByPhoneNumber(String phoneNumber);

    Page<Provider> findByIsOnline(boolean online, Pageable pageable);
    List<Provider> findByServiceIdAndIsActiveTrueAndIsEnabledTrueAndIsLockedFalseAndIsVerifiedTrueAndIsSubmittedTrue(Long serviceId);

}
