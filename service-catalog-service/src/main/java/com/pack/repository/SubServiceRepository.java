package com.pack.repository;

import com.pack.entity.SubService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubServiceRepository extends JpaRepository<SubService,Long> {
    Optional<SubService> findByName(String name);

    List<SubService> findAllByServiceId(Long serviceId);

    Page<SubService> findAllByItIsAvailable(boolean isAvailable, Pageable pageable);

    Page<SubService> findAllByServiceIdAndItIsAvailable(Long serviceId, boolean isAvailable, Pageable pageable);

    Page<SubService> findAllByItIsAvailableTrue(Pageable pageable);

    Optional<SubService> findByCode(String code);
}
