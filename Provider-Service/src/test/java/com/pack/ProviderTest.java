package com.pack;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pack.common.dto.ProviderRequestDTO;
import com.pack.common.dto.ProviderResponseDTO;
import com.pack.common.enums.Role;
import com.pack.controller.ProviderController;
import com.pack.entity.Provider;
import com.pack.enums.ProviderType;
import com.pack.enums.VerificationStatus;
import com.pack.repository.ProviderRepository;
import com.pack.service.ProviderService;
import com.pack.utils.OtpService;
import com.pack.utils.ProviderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProviderController.class)
class ProviderControllerTest {


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderService providerService;

    private Provider sampleProvider;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private OtpService otpService;

    private ProviderResponseDTO responseDTO;

    @MockBean
    private ProviderRepository providerRepository;
    @BeforeEach
    void setUp() {
        sampleProvider = Provider.builder()
                .id(1L)
                .fullName("John Doe")
                .isEnabled(true)
                .isActive(true)
                .isLocked(false)
                .isOnline(true)
                .providerType(ProviderType.INDIVIDUAL)
                .verificationStatus(VerificationStatus.SUCCESS)
                .role(Role.PROVIDER)
                .build();

        responseDTO = ProviderMapper.toDto(sampleProvider);
    }

    @WithMockUser(username = "admin", roles = {"PROVIDER"})
    @Test
    void testGetById() throws Exception {
        when(providerService.getById(1L)).thenReturn(sampleProvider);

        mockMvc.perform(get("/provider/v1/1/get-provider"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe"));
    }

    @WithMockUser(username = "admin", roles = {"PROVIDER"})
    @Test
    void testGetByServiceId() throws Exception {
        when(providerService.getByServiceId("SS-110")).thenReturn(List.of(sampleProvider));

        mockMvc.perform(get("/provider/v1/100/get"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName").value("John Doe"));
    }


    @WithMockUser(username = "admin", roles = {"PROVIDER", "ADMIN"})
    @Test
    void testGetProvidersByServiceId() throws Exception {
        String serviceId = "SS-1";

        // Mock Provider entities (not DTOs)
        Provider provider1 = Provider.builder()
                .id(1L)
                .fullName("Test Provider 1")
                .providerType(ProviderType.INDIVIDUAL)
                .verificationStatus(VerificationStatus.SUCCESS)
                .build();
        Provider provider2 = Provider.builder()
                .id(2L)
                .fullName("Test Provider 2")
                .providerType(ProviderType.INDIVIDUAL)
                .verificationStatus(VerificationStatus.SUCCESS)
                .build();

        List<Provider> mockProviders = List.of(provider1, provider2);

        when(providerService.getByServiceId(serviceId)).thenReturn(mockProviders);

        mockMvc.perform(get("/provider/v1/{serviceId}/get", serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fullName").value("Test Provider 1"))
                .andExpect(jsonPath("$[1].id").value(2L));
    }


    @WithMockUser(username = "admin", roles = {"ADMIN", "PROVIDER"})
    @Test
    void testGetAllProviders_NoOnlineStatusFilter() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Provider> page = new PageImpl<>(List.of(sampleProvider));

        when(providerService.getAllPaged(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/provider/v1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName").value("John Doe"));
    }

    @WithMockUser(username = "admin", roles = {"PROVIDER"})
    @Test
    void testUpdateProvider() throws Exception {
        ProviderRequestDTO requestDTO = ProviderRequestDTO.builder()
                .fullName("Updated Name")
                .build();

        Provider updatedProvider = sampleProvider.toBuilder().fullName("Updated Name").build();

        when(providerService.updateProvider(eq(1L), any())).thenReturn(updatedProvider);

        mockMvc.perform(put("/provider/v1/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"));
    }


    @WithMockUser(username = "admin", roles = {"PROVIDER"})
    @Test
    void testEnableForService() throws Exception {
        when(providerService.enableProvider(1L, true)).thenReturn("Enabled");

        mockMvc.perform(post("/provider/v1/enable/1")
                        .param("isEnable", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Enabled You are now enabled for service."));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void testLockProvider() throws Exception {
        mockMvc.perform(post("/provider/v1/lock/1")
                        .param("reason", "Violation"))
                .andExpect(status().isOk())
                .andExpect(content().string("Provider locked"));

        Mockito.verify(providerService).lockProvider(1L, "Violation");
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    void testUnlockProvider() throws Exception {
        mockMvc.perform(post("/provider/v1/unlock/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Provider unlocked"));

        Mockito.verify(providerService).unlockProvider(1L);
    }

    @WithMockUser(username = "admin", roles = {"PROVIDER","ADMIN"})
    @Test
    void testDeleteProvider() throws Exception {
        mockMvc.perform(delete("/provider/v1/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Provider deleted"));

        Mockito.verify(providerService).deleteProvider(1L);
    }

    @WithMockUser(username = "admin", roles = {"PROVIDER","ADMIN"})
    @Test
    void testUpdateStatus() throws Exception {
        sampleProvider.setIsOnline(false);
        when(providerService.updateStatus(1L, false)).thenReturn(sampleProvider);

        mockMvc.perform(patch("/provider/v1/1/status")
                        .param("is_online", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isOnline").value(false));
    }
}
