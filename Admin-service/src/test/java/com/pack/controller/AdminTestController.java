package com.pack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pack.auth.JwtAuthFilter;
import com.pack.auth.JwtUtil;
import com.pack.dto.AdminResponseDTO;
import com.pack.entity.Admin;
import com.pack.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private Admin admin;

    @BeforeEach
    void setup() {
        admin = new Admin();
        admin.setId(1L);
        admin.setPhoneNumber("1234567890");
        admin.setUsername("admin");
        admin.setVerified(true);
        admin.setGrantAccess(true);
    }

    @Test
    void testGrantAccess() throws Exception {
        mockMvc.perform(post("/admin/v1/grant-access/1?targetId=2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Access granted to admin ID 2"));
    }

    @Test
    void testRemoveAccess() throws Exception {
        mockMvc.perform(post("/admin/v1/remove-access/1?targetId=2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Access removed from admin ID 2"));
    }

    @Test
    void testChangeNumber() throws Exception {
        Mockito.when(adminService.changeNumber(1L, "1234567890", "0987654321"))
                .thenReturn(admin);

        mockMvc.perform(post("/admin/v1/1/change-number")
                        .param("old_number", "1234567890")
                        .param("new_number", "0987654321"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Phone number updated successfully"))
                .andExpect(jsonPath("$.updatedAdmin.phoneNumber").value("1234567890"));
    }

    @Test
    void testUpdatePassword() throws Exception {
        Mockito.when(adminService.changePassword(1L, "oldPass", "newPass"))
                .thenReturn(admin);

        mockMvc.perform(post("/admin/v1/1/update-password")
                        .param("old", "oldPass")
                        .param("new", "newPass"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully"))
                .andExpect(jsonPath("$.updatedAdmin.username").value("admin"));
    }
}
