package com.pack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pack.auth.JwtUtil;
import com.pack.dto.AdminCreateDTO;
import com.pack.entity.Admin;
import com.pack.service.AdminService;
import com.pack.utils.OtpService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private OtpService otpService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendOtp_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/admin/auth/1234567890/send-otp"))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP sent to the provided mobile number."));
    }

    @Test
    void checkUsername_shouldReturnConflictIfExists() throws Exception {
        Mockito.when(adminService.checkUsername("taken")).thenReturn(true);

        mockMvc.perform(post("/admin/auth/check-username")
                        .param("username", "taken"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already in use"));
    }

    @Test
    void checkUsername_shouldReturnAvailableIfNotExists() throws Exception {
        Mockito.when(adminService.checkUsername("free")).thenReturn(false);

        mockMvc.perform(post("/admin/auth/check-username")
                        .param("username", "free"))
                .andExpect(status().isOk())
                .andExpect(content().string("Username is available"));
    }

    @Test
    void createAdmin_shouldReturnSuccess() throws Exception {
        AdminCreateDTO dto = new AdminCreateDTO("1234567890", "Admin", "pass", "123456");

        Mockito.when(otpService.validateOtp("1234567890", "123456")).thenReturn(true);
        Admin admin = new Admin(
                1L, // id
                "Admin", // name
                "1234567890",//
                "123456", // password
                true, // isActive
                LocalDateTime.now(), // createdAt
                LocalDateTime.now(), // updatedAt
                new ArrayList<>(), // loginHistories
                true, // isVerified
                false // isDeleted
        );
        String validJson = "{ \"username\": \"Admin\", \"password\": \"123456\", \"phoneNumber\": \"1234567890\", \"otp\": \"123456\" }";
        Mockito.when(adminService.addAdmin(any(Admin.class))).thenReturn(admin);

        mockMvc.perform(post("/admin/auth/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andDo(print()) // this prints full request/response
                .andExpect(status().isOk());

    }

    @Test
    void createAdmin_shouldFailOnInvalidOtp() throws Exception {
        String invalidJson = "{ \"name\": \"Admin\", \"password\": \"123456\", \"phone\": \"1234567890\", \"otp\": \"123456\" }";
        AdminCreateDTO dto = new AdminCreateDTO("admin", "pass", "1234567890", "999999");
        Mockito.when(otpService.validateOtp(anyString(), anyString())).thenReturn(false);
        mockMvc.perform(post("/admin/auth/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void uLogin_shouldReturnTokens() throws Exception {
        Admin admin = new Admin(
                1L, // id
                "John Doe", // name
                "john@example.com", // email
                "password123", // password
                true, // isActive
                LocalDateTime.now(), // createdAt
                LocalDateTime.now(), // updatedAt
                new ArrayList<>(), // loginHistories
                true, // isVerified
                false // isDeleted
        );

        Mockito.when(adminService.verifyAdminByUsername("admin", "pass")).thenReturn(admin);
        Mockito.when(jwtUtil.generateToken(anyString(), anyList())).thenReturn("access-token");
        Mockito.when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh-token");

        mockMvc.perform(post("/admin/auth/u_login")
                        .param("username", "admin")
                        .param("password", "pass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void pLogin_shouldReturnTokens() throws Exception {
        Mockito.when(otpService.validateOtp("1234567890", "123456")).thenReturn(true);

        Admin admin = new Admin(
                1L, // id
                "John Doe", // name
                "john@example.com", // email
                "password123", // password
                true, // isActive
                LocalDateTime.now(), // createdAt
                LocalDateTime.now(), // updatedAt
                new ArrayList<>(), // loginHistories
                true, // isVerified
                false // isDeleted
        );

        Mockito.when(adminService.verifyAdminByPhoneNumber("1234567890")).thenReturn(admin);
        Mockito.when(jwtUtil.generateToken(anyString(), anyList())).thenReturn("access-token");
        Mockito.when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refresh-token");

        mockMvc.perform(post("/admin/auth/p_login")
                        .param("number", "1234567890")
                        .param("otp", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void pLogin_shouldFailOnInvalidOtp() throws Exception {
        Mockito.when(otpService.validateOtp(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/admin/auth/p_login")
                        .param("number", "1234567890")
                        .param("otp", "000000"))
                .andExpect(status().isBadRequest());
    }
}

