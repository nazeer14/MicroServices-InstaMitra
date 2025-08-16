package com.pack.controller;

import com.pack.common.dto.UserDto;
import com.pack.common.response.ApiResponse;
import com.pack.entity.User;
import com.pack.service.UserService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ================= ADMIN CHECK =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-access")
    public ResponseEntity<ApiResponse<?>> checkAdminAccess() {
        return ResponseEntity.ok(ApiResponse.ok("Welcome Admin!", null));
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(ApiResponse.ok("User fetched successfully", convertToDto(user)));
    }

    // ================= GET USER BY PHONE =================
    @GetMapping("/by-phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByPhone(@PathVariable @NotNull(message = "Number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phoneNumber) {
        User user = userService.getByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponse.ok("User fetched successfully", convertToDto(user)));
    }

    // ================= GET ALL USERS (with optional pagination) =================
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<User> users = (page != null && size != null)
                ? userService.getAllUsersPaginated(page, size)
                : userService.getAllUser();
        List<UserDto> userDtos = users.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok("Users fetched successfully", userDtos));
    }


//    // ================= UPDATE USER =================
//    @PutMapping("/{id}")
//    public ResponseEntity<ApiResponse<UserDto>> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
//        User updatedUser = userService.updateUser(id, userDto);
//        return ResponseEntity.ok(ApiResponse.ok("User updated successfully", convertToDto(updatedUser)));
//    }

    // ================= DELETE USER =================
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted successfully", null));
    }

    // ================= LOCK / UNLOCK BY ID =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/lock")
    public ResponseEntity<ApiResponse<?>> lockUserById(@PathVariable Long id) {
        userService.lockTheUserById(id);
        return ResponseEntity.ok(ApiResponse.ok("User locked successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/unlock")
    public ResponseEntity<ApiResponse<?>> unlockUserById(@PathVariable Long id) {
        userService.unlockTheUserById(id);
        return ResponseEntity.ok(ApiResponse.ok("User unlocked successfully", null));
    }

    // ================= LOCK / UNLOCK BY PHONE =================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/by-phone/{phoneNumber}/lock")
    public ResponseEntity<ApiResponse<?>> lockUserByPhone(@PathVariable @NotNull(message = "Number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phoneNumber) {
        userService.lockTheUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponse.ok("User locked successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/by-phone/{phoneNumber}/unlock")
    public ResponseEntity<ApiResponse<?>> unlockUserByPhone(@PathVariable @NotNull(message = "Number is required") @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits") String phoneNumber) {
        userService.unlockTheUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(ApiResponse.ok("User unlocked successfully", null));
    }

    // ================= PRIVATE HELPERS =================
    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .phone(user.getPhoneNumber())
                .isVerified(user.isVerified())
                .build();
    }
}
