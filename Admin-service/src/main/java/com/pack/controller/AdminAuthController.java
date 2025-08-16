package com.pack.controller;

import com.pack.auth.JwtUtil;
import com.pack.common.dto.JwtResponse;
import com.pack.common.dto.RefreshTokenRequest;
import com.pack.common.util.OtpGenerator;
import com.pack.dto.AdminCreateDTO;
import com.pack.dto.AdminResponseDTO;
import com.pack.dto.LoginResponseDTO;
import com.pack.entity.Admin;
import com.pack.service.AdminService;
import com.pack.utils.OtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    @Value("${super.admin.number}")
    private String superAdminNumber;

    @Value("${super.admin.key}")
    private String superAdminKey;


    public static final List<String> SUPER_ADMIN_ROLES = List.of("USER", "ADMIN", "PROVIDER", "SUPERADMIN");
    public static final List<String> ADMIN_ROLES = List.of("ADMIN");

    /**
     * Send OTP to a phone number for verification.
     */
    @PostMapping("/{phoneNumber}/send-otp")
    public ResponseEntity<String> sendOtp(
            @PathVariable("phoneNumber")
            @Valid @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
            String phoneNumber
    ) {
        if(!adminService.checkAdminNumber(phoneNumber)){
            return ResponseEntity.status(401).body("Invalid phone number.Please contact SUPER ADMIN");
        }
        String otp = OtpGenerator.generateOtp(6);
        otpService.generateAndStoreOtp(phoneNumber, otp);
        log.info("Admin OTP sent to phone number ending in ****{}", phoneNumber.substring(6)); // Masked logging
        return ResponseEntity.ok("OTP sent to the provided mobile number.");
    }

    /**
     * Authenticate an admin using phone number and OTP.
     */
    @PostMapping("/verify")
    public ResponseEntity<LoginResponseDTO> verifyByNumber(
            @RequestParam("number")
            @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String number,
            @RequestParam String otp
    ) {
        if (!otpService.validateOtp(number, otp)) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        Admin admin = adminService.verifyAdminByPhoneNumber(number);
        AdminResponseDTO response = new AdminResponseDTO(
                admin.getId(),
                admin.getPhoneNumber(),
                admin.isVerified(),
                admin.isGrantAccess()
        );

        String accessToken=jwtUtil.generateToken(number,ADMIN_ROLES);
        String refreshToken=jwtUtil.generateRefreshToken(number);
        return ResponseEntity.ok(new LoginResponseDTO(accessToken,refreshToken,response));
    }

    /**
     * Send OTP to a phone number for verification.
     */


    @PostMapping("/a1/{phoneNumber}/send-otp")
    public ResponseEntity<String> sendSuperOtp(
            @PathVariable
            @Valid @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
            String phoneNumber,
            @RequestParam String ipAddress
    ) {
        if(!phoneNumber.equals(superAdminNumber)){
            log.info("This number try to login {}", phoneNumber);
            log.info("The IP Address is {}",ipAddress);
            return ResponseEntity.status(401).body("This Number don't have permission. Please check");
        }
        String otp = OtpGenerator.generateOtp(6);
        otpService.generateAndStoreOtp(phoneNumber, otp);

        log.info("OTP sent to phone number ending in ****{}", phoneNumber.substring(6)); // Masked logging
        return ResponseEntity.ok("OTP sent to the provided mobile number.");
    }

    @PostMapping("/super-admin/verify")
    public ResponseEntity<?> verifySuperAdmin(@RequestParam @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phoneNumber,
                                              @RequestParam @Pattern(regexp = "^[0-6]{6}$", message = "OTP must be 6 digits")String otp,
                                              @RequestParam @Pattern(regexp = "^[(0-9a-zA-Z)]{12}$", message = "key must be 12 characters") String key){
       if(!key.equals(superAdminKey)){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid key");
       }

       if(!otpService.validateOtp(phoneNumber,otp)){
           return ResponseEntity.badRequest().body("Incorrect OTP");
       }

        String accessToken=jwtUtil.generateToken(phoneNumber,SUPER_ADMIN_ROLES );
        String refreshToken=jwtUtil.generateRefreshToken(phoneNumber);

        Map<String,Object> response=new HashMap<>();
       response.put("id",1);
       response.put("phoneNumber",phoneNumber);
       response.put("accessToken",accessToken);
       response.put("refreshToken",refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/a/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        String phone = jwtUtil.extractUserId(refreshToken);

        String newAccessToken = jwtUtil.generateToken(phone, ADMIN_ROLES);

        return ResponseEntity.ok(
                new JwtResponse(newAccessToken, refreshToken, "Bearer", ADMIN_ROLES));

    }

    @PostMapping("/super_a/refresh")
    public ResponseEntity<?> superAdminTokenRefresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
           return ResponseEntity.status(401).body("Invalid refresh token");
        }

        String phone = jwtUtil.extractUserId(refreshToken);

        List<String> roles = SUPER_ADMIN_ROLES;


        String newAccessToken = jwtUtil.generateToken(phone, roles);

        return ResponseEntity.ok(
                new JwtResponse(newAccessToken, refreshToken, "Bearer", roles));

    }
}
