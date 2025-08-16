package com.pack.controller;

import com.pack.auth.JwtUtil;
import com.pack.common.dto.AuthRequest;
import com.pack.common.dto.JwtResponse;
import com.pack.common.dto.RefreshTokenRequest;
import com.pack.dto.LoginResponse;
import com.pack.entity.User;
import com.pack.service.UserService;
import com.pack.utils.OtpService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    // ===================== SEND OTP =====================
    @PostMapping("/otp/send/{phoneNumber}")
    public ResponseEntity<?> sendOtp(
            @PathVariable("phoneNumber")
            @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
            String phoneNumber) {

        otpService.generateAndStoreOtp(phoneNumber);
        // TODO: Integrate actual OTP provider (e.g., Twilio, Firebase)

        return ResponseEntity.ok(buildSuccessMessage("OTP sent to " + phoneNumber));
    }

    // ===================== VERIFY OTP & LOGIN =====================
    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOtpAndLogin(@RequestBody @Valid AuthRequest request) {
        // Validate OTP
        if (!otpService.validateOtp(request.getPhone(), request.getOtp())) {
            return buildErrorResponse("Incorrect OTP", HttpStatus.BAD_REQUEST);
        }

        // Validate or create user
        User user = userService.validateAndAddUser(request.getPhone());
        if (user == null) {
            return buildErrorResponse("Verification failed. Try again", HttpStatus.UNAUTHORIZED);
        }

        // Check if locked
        if (user.isLocked()) {
            return buildErrorResponse("This account is locked. Contact customer support.", HttpStatus.LOCKED);
        }

        // Roles for JWT
        List<String> roles = List.of("USER");

        // Generate tokens
        String accessToken = jwtUtil.generateToken(request.getPhone(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(request.getPhone());

        return ResponseEntity.ok(
                new LoginResponse(accessToken, refreshToken, user, LocalDateTime.now())
        );
    }

    // ===================== REFRESH TOKEN =====================
    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            return buildErrorResponse("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String phone = jwtUtil.extractUserId(refreshToken);
        List<String> roles = List.of("USER");
        String newAccessToken = jwtUtil.generateToken(phone, roles);

        return ResponseEntity.ok(
                new JwtResponse(newAccessToken, refreshToken, "Bearer", roles)
        );
    }

    // ===================== PRIVATE HELPERS =====================
    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(status.value(), message));
    }

    private ResponseEntity<Object> buildSuccessMessage(String message) {
        return ResponseEntity.ok(new SuccessResponse(HttpStatus.OK.value(), message));
    }

    // Inner classes for consistent API responses
    @lombok.Data
    @lombok.AllArgsConstructor
    static class ErrorResponse {
        private int status;
        private String message;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    static class SuccessResponse {
        private int status;
        private String message;
    }
}
