package com.pack.service;

import com.pack.entity.Admin;
import org.springframework.transaction.annotation.Transactional;

public interface AdminService {

    boolean checkAdminNumber(String phoneNumber);


    Admin verifyAdminByPhoneNumber(String phoneNumber);

    @Transactional
    void grantAccess(Long accessId, Long adminId);

    Admin updateAdmin(Admin admin);

    Admin changeNumber(Long id,String oldNumber,String newNumber);

    void deleteAdmin(Admin admin);


    @Transactional
    void removeAccess(Long accessId, Long adminId);

    void grantAccessById(Long adminId);

    void removeAccessById(Long adminId);

    void addNewAdminNumber(String number);
}
