package com.pack.service.impl;

import com.pack.entity.Admin;
import com.pack.repository.AdminRepository;
import com.pack.service.AdminService;
import com.pack.service.LoginHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;

    private final LoginHistoryService loginHistoryService;

    @Override
    public void addNewAdminNumber(String number) {
        Admin admin=new Admin();
        admin.setPhoneNumber(number);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setVerified(true);
        admin.setGrantAccess(true);
        admin.setAccessAdmin(true);
        adminRepository.save(admin);
    }
    @Override
    public boolean checkAdminNumber(String phoneNumber) {
        return adminRepository.existsByPhoneNumber(phoneNumber);
    }


    @Override
    public Admin verifyAdminByPhoneNumber(String phoneNumber) {
        Admin admin = adminRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with phone number: " + phoneNumber));

        if (!admin.isVerified() || !admin.isGrantAccess()) {
            throw new LockedException("Admin account not verified or access not granted.");
        }
        loginHistoryService.saveLogin(admin.getId());
        return admin;
    }

    @Transactional
    @Override
    public void grantAccess(Long accessId, Long adminId) {

        // Admin who wants to grant access
        Admin grantingAdmin = adminRepository.findById(accessId)
                .orElseThrow(() -> new AccessDeniedException("Invalid granting admin ID"));

        if (!grantingAdmin.isAccessAdmin() || !grantingAdmin.isVerified()) {
            throw new AccessDeniedException("Granting admin does not have permission");
        }

        // Admin to whom access should be granted
        Admin targetAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Target admin not found with ID: " + adminId));

        if (!targetAdmin.isVerified()) {
            throw new IllegalStateException("Target admin is not verified");
        }

        if (targetAdmin.isGrantAccess()) {
            throw new IllegalStateException("Target admin already has access");
        }

        targetAdmin.setVerified(true);
        targetAdmin.setGrantAccess(true);
        adminRepository.save(targetAdmin);
        log.info("Access granted to adminId={} by adminId={}", adminId, accessId);
    }


    @Override
    public Admin updateAdmin(Admin updatedAdmin) {
        Admin existing = adminRepository.findById(updatedAdmin.getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));
        // Enforce 7-day update restriction

        LocalDateTime lastUpdated = existing.getUpdatedAt();
        LocalDateTime nextAllowedUpdate = lastUpdated.plusDays(7);

        if (LocalDateTime.now().isBefore(nextAllowedUpdate)) {
            throw new IllegalStateException("You can't update the number now. Try after: " + nextAllowedUpdate);
        }
        existing.setUpdatedAt(LocalDateTime.now());
        return adminRepository.save(existing);
    }

    @Override
    public Admin changeNumber(Long id, String oldNumber, String newNumber) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with ID: " + id));

        // Verify old phone number matches
        if (!admin.getPhoneNumber().equals(oldNumber)) {
            throw new SecurityException("Provided old phone number does not match.");
        }

        // Enforce 7-day update restriction
        LocalDateTime lastUpdated = admin.getUpdatedAt();
        LocalDateTime nextAllowedUpdate = lastUpdated.plusDays(7);

        if (LocalDateTime.now().isBefore(nextAllowedUpdate)) {
            throw new IllegalStateException("You can't update the number now. Try after: " + nextAllowedUpdate);
        }

        // Update phone number
        admin.setPhoneNumber(newNumber);
        admin.setUpdatedAt(LocalDateTime.now());

        log.info("Admin phone number updated for adminId={} from {} to {}", id, oldNumber, newNumber);

        return adminRepository.save(admin);
    }


    @Override
    public void deleteAdmin(Admin admin) {
        if (!adminRepository.existsById(admin.getId())) {
            throw new EntityNotFoundException("Admin not found");
        }

        adminRepository.delete(admin);
    }


    @Transactional
    @Override
    public void removeAccess(Long accessId, Long adminId) {

        // Admin requesting the removal
        Admin grantingAdmin = adminRepository.findById(accessId)
                .orElseThrow(() -> new AccessDeniedException("Invalid granting admin ID"));

        if (!grantingAdmin.isAccessAdmin() || !grantingAdmin.isVerified()) {
            throw new AccessDeniedException("Granting admin does not have sufficient privileges");
        }

        // Admin whose access is being revoked
        Admin targetAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Target admin not found with ID: " + adminId));

        if (!targetAdmin.isGrantAccess()) {
            throw new IllegalStateException("Target admin does not currently have access");
        }

        targetAdmin.setVerified(false);
        targetAdmin.setGrantAccess(false);
        adminRepository.save(targetAdmin);

        log.info("Access revoked for adminId={} by adminId={}", adminId, accessId);
    }

    @Override
    public void grantAccessById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        admin.setGrantAccess(true);
        adminRepository.save(admin);
        log.info("Access granted to admin id {}", adminId);
    }

    @Override
    public void removeAccessById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found"));

        admin.setGrantAccess(false);
        adminRepository.save(admin);
        log.info("Access removed from admin id {}", adminId);
    }




}
