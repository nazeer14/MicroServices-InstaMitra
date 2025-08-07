package com.pack.repository;

import com.pack.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Admin> findByPhoneNumber(String phoneNumber);

}
