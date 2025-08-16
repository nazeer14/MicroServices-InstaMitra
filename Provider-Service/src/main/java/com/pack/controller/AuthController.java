package com.pack.controller;

import com.pack.auth.JwtUtil;
import com.pack.common.dto.AuthRequest;
import com.pack.common.dto.JwtResponse;
import com.pack.common.dto.RefreshTokenRequest;
import com.pack.common.enums.Role;
import com.pack.dto.LoginResponse;
import com.pack.entity.Provider;
import com.pack.service.ProviderService;
import com.pack.service.ProviderServiceImpl;
import com.pack.utils.OtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;

    private final ProviderService providerService;

    private final OtpService otpService;

    @PostMapping("/send/{phoneNumber}")
    public ResponseEntity<String> sendOtp(@PathVariable("phoneNumber")  @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phoneNumber){

        System.out.println(phoneNumber);
        otpService.generateAndStoreOtp(phoneNumber);

        //use firebase
        return ResponseEntity.ok("Otp sent to "+phoneNumber);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {

        if(!otpService.validateOtp(request.getPhone(),request.getOtp())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect OTP");
        }

       Provider provider= providerService.validateAndAdd(request.getPhone());
        if(provider==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Verification failed. Try again");
        }
        if(provider.isLocked()){
            return ResponseEntity.status(HttpStatus.LOCKED).body("This number was Locked. Contact customer support.");
        }

        List<String> roles = List.of("PROVIDER");


        String accessToken = jwtUtil.generateToken(request.getPhone(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(request.getPhone());

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, provider));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String phone = jwtUtil.extractUserId(refreshToken);


        List<String> roles = List.of("PROVIDER");


        String newAccessToken = jwtUtil.generateToken(phone, roles);
        String newRefreshToken = jwtUtil.generateRefreshToken(phone);

        return ResponseEntity.ok(
                new JwtResponse(newAccessToken, newRefreshToken, "Bearer", roles)
        );
    }

}
