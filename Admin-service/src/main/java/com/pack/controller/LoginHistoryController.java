package com.pack.controller;

import com.pack.entity.LoginHistory;
import com.pack.service.LoginHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/login-history")
@RequiredArgsConstructor
@Tag(name = "Login History", description = "Track admin login times")
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    @PostMapping("/record/{adminId}")
    @Operation(summary = "Record admin login")
    public ResponseEntity<LoginHistory> recordLogin(@PathVariable Long adminId) {
        return ResponseEntity.ok(loginHistoryService.saveLogin(adminId));
    }

    @GetMapping("/admin/{adminId}")
    @Operation(summary = "Get login history by admin ID")
    public ResponseEntity<List<LoginHistory>> getByAdmin(@PathVariable Long adminId) {
        return ResponseEntity.ok(loginHistoryService.getLoginHistoryByAdmin(adminId));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all login history (admin-only)")
    public ResponseEntity<List<LoginHistory>> getAll() {
        return ResponseEntity.ok(loginHistoryService.getAllLogins());
    }
}
