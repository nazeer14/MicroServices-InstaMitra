package com.pack.controller;

import com.pack.auth.JwtUtil;
import com.pack.common.dto.AuthRequest;
import com.pack.common.dto.JwtResponse;
import com.pack.common.dto.RefreshTokenRequest;
import com.pack.common.enums.Role;
import com.pack.dto.LoginResponse;
import com.pack.entity.User;
import com.pack.repository.UserRepo;
import com.pack.service.UserService;
import com.pack.utils.OtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    private final OtpService otpService;

    @PostMapping("/send/{phoneNumber}")
    public ResponseEntity<String> sendOtp(@PathVariable("phoneNumber") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phoneNumber) {

        otpService.generateAndStoreOtp(phoneNumber);
        //use third party services to send otp. Like twilio, firebase

        return ResponseEntity.ok("Otp sent to " + phoneNumber);
    }

    @PostMapping("/verify")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        //validate user with otp
        if (!otpService.validateOtp(request.getPhone(), request.getOtp())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect OTP");
        }

        User user = userService.validateAndAddUser(request.getPhone());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Verification failed. Try again");
        }
        if (user.isLocked()) {
            return ResponseEntity.status(HttpStatus.LOCKED).body("This number was Locked. Contact customer support.");
        }

        List<String> roles = List.of("USER");

        String accessToken = jwtUtil.generateToken(request.getPhone(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(request.getPhone());

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken, user, LocalDateTime.now()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String phone = jwtUtil.extractUserId(refreshToken);

        List<String> roles = List.of("USER");


        String newAccessToken = jwtUtil.generateToken(phone, roles);

        return ResponseEntity.ok(
                new JwtResponse(newAccessToken, refreshToken, "Bearer", roles));

    }
}
