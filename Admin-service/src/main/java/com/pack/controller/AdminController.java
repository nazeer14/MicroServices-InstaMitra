package com.pack.controller;

import com.pack.dto.AdminResponseDTO;
import com.pack.entity.Admin;
import com.pack.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/v1")
@RequiredArgsConstructor
@Slf4j
@RestControllerAdvice
public class AdminController {

    private final AdminService adminService;



    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @PostMapping("/grant-access/{adminId}")
    public ResponseEntity<String> grantAccess(@PathVariable("adminId") Long grantingAdminId,
                                              @RequestParam("targetId") Long targetId) {
        adminService.grantAccess(grantingAdminId, targetId);
        log.info("Access granted to adminId={} by adminId={}", targetId, grantingAdminId);
        return ResponseEntity.ok("Access granted to admin ID " + targetId);
    }


    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @PostMapping("/add")
    public ResponseEntity<String> addNumber(@RequestParam String number){
        adminService.addNewAdminNumber(number);
        return ResponseEntity.ok("new admin number "+number+" added successfully ");
    }

    @PreAuthorize("hasAuthority('SUPERADMIN')")
    @PostMapping("/remove-access/{adminId}")
    public ResponseEntity<String> removeAccess(@PathVariable("adminId") Long grantingAdminId,
                                               @RequestParam("targetId") Long targetId) {
        adminService.removeAccess(grantingAdminId, targetId);
        log.info("Access removed from adminId={} by adminId={}", targetId, grantingAdminId);
        return ResponseEntity.ok("Access removed from admin ID " + targetId);
    }


}
