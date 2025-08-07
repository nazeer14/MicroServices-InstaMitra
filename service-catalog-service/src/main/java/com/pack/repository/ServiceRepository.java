package com.pack.repository;

import com.pack.entity.ServiceCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServiceRepository extends JpaRepository<ServiceCatalog,Long> {

    Optional<ServiceCatalog> findByName(String name);

    boolean existsById(Long id);

    Optional<ServiceCatalog> findByServiceCode(String code);
}
